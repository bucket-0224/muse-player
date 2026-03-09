package kr.co.donghyun.player.data.channel.database

import androidx.room.Database
import androidx.room.RoomDatabase
import kr.co.donghyun.player.data.channel.dao.ChannelDao
import kr.co.donghyun.player.data.channel.model.SearchItem

@Database(entities = [SearchItem::class], version = 4, exportSchema = false)
abstract class ChannelDatabase : RoomDatabase(){
    abstract fun channelDao(): ChannelDao
}