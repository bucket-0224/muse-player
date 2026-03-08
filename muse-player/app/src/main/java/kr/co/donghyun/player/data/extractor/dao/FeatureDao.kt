package kr.co.donghyun.player.data.extractor.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kr.co.donghyun.player.data.extractor.model.FeatureResponse

@Dao
interface FeatureDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(featureResponse: FeatureResponse)

    @Query("SELECT * FROM FeatureResponse")
    suspend fun fetchAll(): List<FeatureResponse>

    @Query("DELETE FROM FeatureResponse")
    suspend fun deleteAll()
}