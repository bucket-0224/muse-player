package kr.co.donghyun.player.data.extractor.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kr.co.donghyun.player.data.extractor.dao.ExtractorDao
import kr.co.donghyun.player.data.extractor.database.ExtractorDatabase
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RoomModule {
    @Singleton
    @Provides
    fun provideExtractorDao(extractorDatabase: ExtractorDatabase): ExtractorDao = extractorDatabase.extractorDao()

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): ExtractorDatabase
        = Room.databaseBuilder(context, ExtractorDatabase::class.java, "extractor_db")
            .fallbackToDestructiveMigration()
            .build()
}