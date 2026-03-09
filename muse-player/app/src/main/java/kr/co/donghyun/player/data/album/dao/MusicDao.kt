package kr.co.donghyun.player.data.album.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kr.co.donghyun.player.data.album.model.Music
import kr.co.donghyun.player.data.channel.model.SearchItem

@Dao
interface MusicDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(music: Music)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(videoItem: SearchItem)

    @Query("SELECT * FROM Music")
    suspend fun fetchAll(): List<Music>

    @Query("SELECT * FROM SearchItem")
    suspend fun fetchAllVideos() : List<SearchItem>

    @Query("DELETE FROM Music")
    suspend fun deleteAll()
}