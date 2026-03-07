package kr.co.donghyun.player.data.extractor.model

import com.google.gson.annotations.SerializedName

data class ShortResponse(
    @SerializedName("videoIds")
    val videoIds : List<String>
)