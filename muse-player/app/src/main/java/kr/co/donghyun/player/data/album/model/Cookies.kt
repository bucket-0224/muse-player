package kr.co.donghyun.player.data.album.model

data class Cookie(
    val domain: String,
    val includeSubdomains: Boolean,
    val path: String,
    val secure: Boolean,
    val expiration: Long,
    val name: String,
    val value: String
)
