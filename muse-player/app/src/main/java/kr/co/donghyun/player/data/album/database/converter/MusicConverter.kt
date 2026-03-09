package kr.co.donghyun.player.data.album.database.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kr.co.donghyun.player.data.album.model.ArtistName
import kr.co.donghyun.player.data.album.model.Duration
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
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }
}