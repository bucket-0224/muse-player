package kr.co.donghyun.player.data.channel.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kr.co.donghyun.player.data.channel.dao.ChannelDao
import kr.co.donghyun.player.data.channel.model.ArtistPreview
import kr.co.donghyun.player.data.extractor.dao.ExtractorDao
import kr.co.donghyun.player.data.extractor.database.converter.ExtractorTypeConverter
import kr.co.donghyun.player.data.extractor.model.ExtractorResponseBody

@Database(entities = [ArtistPreview::class], version = 1)
abstract class ChannelDatabase : RoomDatabase(){
    abstract fun channelDao(): ChannelDao
}