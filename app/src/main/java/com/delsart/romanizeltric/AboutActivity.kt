package com.delsart.romanizeltric

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import mehdi.sakout.aboutpage.AboutPage
import mehdi.sakout.aboutpage.Element


class AboutActivity:Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)




        val authorElement = Element()
        authorElement.setTitle("Delsart").setIntent(OpenUrlIntent("http://www.coolapk.com/u/473036"))
        val romanizeElement = Element()
        romanizeElement.setTitle("kawa.net").setIntent(OpenUrlIntent("http://www.kawa.net/"))

        val apiElement = Element()
        apiElement.setTitle("Hi.Mrdong!").setIntent(OpenUrlIntent("https://www.bzqll.com/"))
        val aboutPage = AboutPage(this)
                .isRTL(false)
                .setDescription("搜索歌词并翻译为罗马音")
                .setImage(R.mipmap.ic_launcher)
                .addGroup("开发者")
                .addItem(authorElement)
                .addGroup("感谢")
                .addItem(romanizeElement)
                .addItem(apiElement)
                .create()
        setContentView(aboutPage)
    }

    fun OpenUrlIntent(url:String):Intent{
        val intent=  Intent()
        intent.setAction("android.intent.action.VIEW");
        intent.setData(Uri.parse(url))
        return  intent
    }
}