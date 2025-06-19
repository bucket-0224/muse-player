package kr.co.donghyun.player.data.extractor.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kr.co.donghyun.player.data.extractor.dao.ExtractorDao
import kr.co.donghyun.player.data.extractor.database.converter.ExtractorTypeConverter
import kr.co.donghyun.player.data.extractor.model.ExtractorResponseBody

@Database(entities = [ExtractorResponseBody::class], version = 1)
abstract class ExtractorDatabase : RoomDatabase(){
    abstract fun extractorDao(): ExtractorDao
}