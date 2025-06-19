package kr.co.donghyun.player.data.common.model

data class Song(
    val videoId: String,
    val title: String,
    val thumbnail: String,
    val author: String,
    val isExplicit: Boolean,
    val duration: String?
)
