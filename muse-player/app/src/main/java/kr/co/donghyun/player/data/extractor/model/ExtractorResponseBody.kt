package kr.co.donghyun.player.data.extractor.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity
data class ExtractorResponseBody(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val videoId : String,
    val thumbnail : String,
    val url : String?,
    val title : String?,
    val artist : String?
) : Serializable