package kr.co.donghyun.player.data.album.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.Date

data class SearchVideoResponse(
    val message: String,
    val videos: List<VideoItem>
)

@Entity
data class VideoItem(
    @PrimaryKey
    val id: String,
    val url: String,
    @SerializedName("shorts_url")
    val shortsUrl: String,
    val title: String,
    val description: String?,
    val duration: Int,
    @SerializedName("duration_formatted")
    val durationFormatted: String,
    val uploadedAt: String?,
    val unlisted: Boolean,
    val nsfw: Boolean,
    val thumbnail: ThumbnailVideo,
    val channel: Channel,
    val views: Int,
    val type: String,
    val tags: List<String>,
    val ratings: Ratings,
    val shorts: Boolean,
    val live: Boolean,
    val `private`: Boolean,
    var insertedAt : Date = Date()
) : Serializable

data class ThumbnailVideo(
    val id: String,
    val width: Int,
    val height: Int,
    val url: String
)

data class Channel(
    val name: String,
    val id: String,
    val icon: String
)

data class Ratings(
    val likes: Int,
    val dislikes: Int
)