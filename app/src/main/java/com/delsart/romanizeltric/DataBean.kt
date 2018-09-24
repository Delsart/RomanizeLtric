package com.delsart.romanizeltric

data class SongInfo(val name: String, val author: String,val url:String,val pic:String,val lrc:String,val resource:String)
data class lyric(val roman:String, val raw: String,val time:String)