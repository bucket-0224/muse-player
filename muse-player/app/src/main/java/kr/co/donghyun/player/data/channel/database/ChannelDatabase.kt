package kr.co.donghyun.player.data.channel.database

import androidx.room.Database
import androidx.room.RoomDatabase
import kr.co.donghyun.player.data.channel.dao.ChannelDao
import kr.co.donghyun.player.data.channel.model.ArtistPreview

@Database(entities = [ArtistPreview::class], version = 1, exportSchema = false)
abstract class ChannelDatabase : RoomDatabase(){
    abstract fun channelDao(): ChannelDao
}