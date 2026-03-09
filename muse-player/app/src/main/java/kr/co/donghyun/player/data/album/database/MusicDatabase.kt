package kr.co.donghyun.player.data.album.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kr.co.donghyun.player.data.album.model.Music
import kr.co.donghyun.player.data.album.dao.MusicDao
import kr.co.donghyun.player.data.album.database.converter.MusicConverter
import kr.co.donghyun.player.data.channel.model.SearchItem

@Database(entities = [Music::class, SearchItem::class], version = 5, exportSchema = false)
@TypeConverters(MusicConverter::class)
abstract class MusicDatabase : RoomDatabase(){
    abstract fun musicDao(): MusicDao
}