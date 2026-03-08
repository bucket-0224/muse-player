package kr.co.donghyun.player.data.extractor.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kr.co.donghyun.player.data.extractor.dao.FeatureDao
import kr.co.donghyun.player.data.extractor.network.FeatureRemote
import kr.co.donghyun.player.data.extractor.repository.FeatureRepository
import kr.co.donghyun.player.data.extractor.repository.SimpleFeatureRepository

@InstallIn(SingletonComponent::class)
@Module
object FeatureModule {
    @Provides
    fun provideExtractorRepository(remote : FeatureRemote, featureDao: FeatureDao) : FeatureRepository {
        return SimpleFeatureRepository(remote, featureDao)
    }
}