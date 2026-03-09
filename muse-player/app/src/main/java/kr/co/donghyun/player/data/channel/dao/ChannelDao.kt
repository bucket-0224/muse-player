package kr.co.donghyun.player.data.channel.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kr.co.donghyun.player.data.channel.model.SearchItem

@Dao
interface ChannelDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(artistItem: SearchItem)

    @Query("SELECT * FROM SearchItem")
    suspend fun fetchAll(): List<SearchItem>

    @Query("DELETE FROM SearchItem")
    suspend fun deleteAll()
}