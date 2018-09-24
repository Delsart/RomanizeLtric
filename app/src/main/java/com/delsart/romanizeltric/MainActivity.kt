package com.delsart.romanizeltric

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import bg
import com.chad.library.adapter.base.BaseQuickAdapter
import io.nichijou.duang.ext.logi
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import then
import java.io.IOException

class MainActivity : AppCompatActivity() {

    val client = OkHttpClient()
    val adapter = MyAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        list.adapter = adapter
        list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val activity = this


        search_button.setOnClickListener {

            if (search_text.text.toString().isNotEmpty()) {
                view_progress.visibility = View.VISIBLE

                bg {

                    request(search_text.text.toString())
                } then {
                    adapter.replaceData(it)
                    view_progress.visibility = View.GONE
                }
            }

        }
        adapter.onItemClickListener = BaseQuickAdapter.OnItemClickListener { adapter, view, position ->
            val intent = Intent(this, LyricActivity::class.java)
            val a = adapter.data[position] as SongInfo
            intent.putExtra("id", a.lrc)
            startActivity(intent)
        }

    }


    fun request(words: String): ArrayList<SongInfo> {
        val jsonObject = JSONObject()
        val bodyObject = JSONObject()
        bodyObject.put("key", words)
        jsonObject.put("TransCode", "020441")
        jsonObject.put("OpenId", "THANKS")
        jsonObject.put("Body", bodyObject)

        val requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString())

        val request = Request.Builder().post(requestBody).url("https://api.hibai.cn/api/index/index").build()
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            return makeBean(response.body()!!.string())
        } else {
            throw  IOException("Unexpected code $response")
        }

    }

    fun makeBean(rawString: String): ArrayList<SongInfo> {
        val list = ArrayList<SongInfo>()

        list.addAll(parseList(JSONObject(rawString).getJSONObject("Body").getJSONArray("qq"), "QQ音乐"))
        list.addAll(parseList(JSONObject(rawString).getJSONObject("Body").getJSONArray("NEC"), "网易云音乐"))
        list.addAll(parseList(JSONObject(rawString).getJSONObject("Body").getJSONArray("kg"), "酷狗音乐"))
        return list
    }

    fun parseList(rawArray: JSONArray, type: String): ArrayList<SongInfo> {
        val list = ArrayList<SongInfo>()

        for (i in 0 until rawArray.length()) {
            val songObject = rawArray[i] as JSONObject
            list.add(SongInfo(songObject.getString("title"), songObject.getString("author"), songObject.getString("url"), songObject.getString("pic"), songObject.getString("lrc"), type))
        }
        return list
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.menu_about)
            startActivity(Intent(this, AboutActivity::class.java))
        return super.onOptionsItemSelected(item)
    }
}
