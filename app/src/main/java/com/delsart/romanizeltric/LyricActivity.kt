package com.delsart.romanizeltric


import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import bg
import com.chad.library.adapter.base.BaseQuickAdapter
import io.nichijou.duang.ext.logi
import kotlinx.android.synthetic.main.activity_ltric.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import then
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context


class LyricActivity : AppCompatActivity() {
    val adapter = LyricAdapter()
    val client = OkHttpClient.Builder()
            .connectTimeout(500, TimeUnit.HOURS)
            .readTimeout(500, TimeUnit.HOURS)
            .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ltric)
        list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        list.adapter = adapter
        val lrc = intent.getStringExtra("id")
        logi { lrc }

        val activity = this

        adapter.onItemClickListener = BaseQuickAdapter.OnItemClickListener { adapter, view, position ->
            Toast.makeText(activity, (adapter.data[position] as lyric).time, Toast.LENGTH_SHORT).show()
        }


        view_progress.visibility = View.VISIBLE
        bg {
            makeContext(lrc)
        } then {
            adapter.replaceData(it)
            view_progress.visibility = View.GONE
        }
    }

    fun makeContext(url: String): ArrayList<lyric> {
        return makeBean(request(url))
    }

    fun request(url: String): String {
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            return response.body()!!.string()
        } else {
            throw  IOException("Unexpected code $response")
        }

    }


    fun makeBean(words: String): ArrayList<lyric> {
        val list = ArrayList<lyric>()
        try {
            val rawString = "[00:00.00]${words.split("\\[\\d\\d:\\d\\d.\\d+]".toRegex(), 2)[1]}"

            val romanLyric = if (rawString.isNotEmpty()) rawString.replace("\\[\\d\\d:\\d\\d.\\d+]".toRegex(), "^").replace("[!@#$%&*:\"~`=\\-;|,<>/?()]".toRegex(), "").roman()
            else arrayListOf("a", "s")
            romanLyric.forEach { logi { it } }
            val list = ArrayList<lyric>()

            val m = Pattern.compile("\\[\\d\\d:\\d\\d.\\d+]").matcher(rawString)
            var i = 1
            while (m.find()) {
                val ly = rawString.makeString(m.group())
                if (ly.isNotEmpty()) {
                    list.add(lyric(romanLyric[i++], ly, m.group()))
                }
            }
            return list

        } catch (e: JSONException) {
            logi { e.toString() }
        }
        return list
    }


    fun String.makeString(regex: String): String {
        val matcher = Pattern.compile("\\$regex.+").matcher(this)
        return if (matcher.find()) {
            matcher.group().toString().replace("\\[\\d\\d:\\d\\d.\\d+]".toRegex(), "")
        } else
            ""
    }

    fun String.roman(): ArrayList<String> {
        logi { this }
        var result = ""
        val list = ArrayList<String>()
        val raw = request("http://www.kawa.net/works/ajax/romanize/romanize.cgi?mode=japanese&q=$this")
        val splitList = raw.split("^")
        splitList.forEach {
            if (it.isNotEmpty()) {
                val m = Pattern.compile("\"[a-z/]+\"").matcher(it)
                while (m.find()) {
                    if (result.isNotEmpty())
                        result += "  "
                    val a = m.group().replace("\"", "")
                    result +=
                            if (m.group().contains("/"))
                                "($a)" else
                                a

                }
                list.add(result.replace("\n", ""))
                result = ""
            }
        }



        return list
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.lyric, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == R.id.menu_paste) {
            var result = ""
            adapter.data.forEach {
                result += "${it.roman}\n${it.raw}\n"
            }
            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("label", result) //文本型数据 clipData 的构造方法。
            clipboardManager!!.setPrimaryClip(clipData) // 将 字符串 str 保存 到剪贴板。
        }
        return super.onOptionsItemSelected(item)
    }
}
