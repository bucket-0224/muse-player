package kr.co.donghyun.player.data.channel.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kr.co.donghyun.player.data.channel.model.ArtistPreview
import kr.co.donghyun.player.data.extractor.model.ExtractorResponseBody

@Dao
interface ChannelDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(artistPreview: ArtistPreview)

    @Query("SELECT * FROM ArtistPreview")
    suspend fun fetchAll(): List<ArtistPreview>

    @Query("DELETE FROM ArtistPreview")
    suspend fun deleteAll()
}