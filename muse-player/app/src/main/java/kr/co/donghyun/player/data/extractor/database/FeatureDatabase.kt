package kr.co.donghyun.player.data.extractor.database

import androidx.room.Database
import androidx.room.RoomDatabase
import kr.co.donghyun.player.data.extractor.dao.FeatureDao
import kr.co.donghyun.player.data.extractor.model.FeatureResponse

@Database(entities = [FeatureResponse::class], version = 2, exportSchema = false)
abstract class FeatureDatabase : RoomDatabase(){
    abstract fun featureDao(): FeatureDao
}