package kr.co.donghyun.player.data.album.database.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kr.co.donghyun.player.data.album.model.ArtistName
import kr.co.donghyun.player.data.album.model.Channel
import kr.co.donghyun.player.data.album.model.Duration
import kr.co.donghyun.player.data.album.model.Ratings
import kr.co.donghyun.player.data.album.model.ThumbnailVideo
import java.util.Date

class MusicConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromArtistNameList(value: List<ArtistName>?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toArtistNameList(value: String): List<ArtistName> {
        val listType = object : TypeToken<List<ArtistName>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromDuration(value: Duration?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toDuration(value: String): Duration {
        return gson.fromJson(value, Duration::class.java)
    }


    @TypeConverter
    fun fromThumbnailVideo(thumbnailVideo: ThumbnailVideo?): String? {
        return gson.toJson(thumbnailVideo)
    }

    @TypeConverter
    fun toThumbnailVideo(json: String?): ThumbnailVideo? {
        if (json.isNullOrEmpty()) return null
        return gson.fromJson(json, ThumbnailVideo::class.java)
    }

    @TypeConverter
    fun fromChannel(channel: Channel?): String? {
        return gson.toJson(channel)
    }

    @TypeConverter
    fun toChannel(json: String?): Channel? {
        if (json.isNullOrEmpty()) return null
        return gson.fromJson(json, Channel::class.java)
    }

    @TypeConverter
    fun fromRatings(ratings: Ratings?): String? {
        return gson.toJson(ratings)
    }

    @TypeConverter
    fun toRatings(json: String?): Ratings? {
        if (json.isNullOrEmpty()) return null
        return gson.fromJson(json, Ratings::class.java)
    }

    @TypeConverter
    fun fromTags(tags: List<String>?): String? {
        return gson.toJson(tags)
    }

    @TypeConverter
    fun toTags(json: String?): List<String>? {
        if (json.isNullOrEmpty()) return emptyList()
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }
}