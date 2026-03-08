package kr.co.donghyun.player.data.extractor.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kr.co.donghyun.player.data.extractor.dao.FeatureDao
import kr.co.donghyun.player.data.extractor.database.FeatureDatabase
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RoomModule {
    @Singleton
    @Provides
    fun provideExtractorDao(featureDatabase: FeatureDatabase): FeatureDao = featureDatabase.featureDao()

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): FeatureDatabase
        = Room.databaseBuilder(context, FeatureDatabase::class.java, "feature_db")
            .fallbackToDestructiveMigration()
            .build()
}