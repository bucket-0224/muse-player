package kr.co.donghyun.player.data.album.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kr.co.donghyun.player.data.album.model.Music
import kr.co.donghyun.player.data.album.dao.MusicDao
import kr.co.donghyun.player.data.album.database.converter.MusicConverter
import kr.co.donghyun.player.data.album.model.VideoItem

@Database(entities = [Music::class, VideoItem::class], version = 2)
@TypeConverters(MusicConverter::class)
abstract class MusicDatabase : RoomDatabase(){
    abstract fun musicDao(): MusicDao
}