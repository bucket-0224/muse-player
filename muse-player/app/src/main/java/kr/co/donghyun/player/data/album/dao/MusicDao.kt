package kr.co.donghyun.player.data.album.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kr.co.donghyun.player.data.album.model.Music
import kr.co.donghyun.player.data.album.model.VideoItem
import kr.co.donghyun.player.data.extractor.model.ExtractorResponseBody

@Dao
interface MusicDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(music: Music)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(videoItem: VideoItem)

    @Query("SELECT * FROM Music")
    suspend fun fetchAll(): List<Music>

    @Query("SELECT * FROM VideoItem")
    suspend fun fetchAllVideos() : List<VideoItem>

    @Query("DELETE FROM Music")
    suspend fun deleteAll()
}