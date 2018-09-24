package com.delsart.romanizeltric

import android.widget.TextView
import bg
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import okhttp3.OkHttpClient
import okhttp3.Request
import then
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

class LyricAdapter : BaseQuickAdapter<lyric, BaseViewHolder>(R.layout.item_lyric) {

    override fun convert(viewHolder: BaseViewHolder, bean: lyric) {

        viewHolder.setText(R.id.lyric_raw, bean.raw)
                .setText(R.id.lyric_roman,bean.roman)



    }



}