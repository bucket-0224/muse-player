package kr.co.donghyun.player.data.channel.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import kr.co.donghyun.player.data.album.database.converter.MusicConverter
import java.util.Date

data class SearchResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("items")
    val items: List<SearchItem>
)

@Entity
@TypeConverters(MusicConverter::class)
data class SearchItem(
    @PrimaryKey
    @SerializedName("id")
    val id: String,

    @SerializedName("type")
    val type: String,  // "artist"가 먼저 나오고, 그 뒤에 "music"이 나옴

    @SerializedName("title")
    val title: String,

    @SerializedName("artist")
    val artist: String?,

    @SerializedName("subscribers")
    val subscribers: String?,

    @SerializedName("durationFormatted")
    val durationFormatted: String?,

    val insertedAt : Date?,

    @SerializedName("thumbnailUrl")
    val thumbnailUrl: String
)