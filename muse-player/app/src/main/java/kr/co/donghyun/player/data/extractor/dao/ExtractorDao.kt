package kr.co.donghyun.player.data.extractor.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kr.co.donghyun.player.data.extractor.model.ExtractorResponseBody

@Dao
interface ExtractorDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(extractorResponse: ExtractorResponseBody)

    @Query("SELECT * FROM ExtractorResponseBody")
    suspend fun fetchAll(): List<ExtractorResponseBody>

    @Query("DELETE FROM ExtractorResponseBody")
    suspend fun deleteAll()
}