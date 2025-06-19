package kr.co.donghyun.player.data.extractor.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kr.co.donghyun.player.data.extractor.dao.ExtractorDao
import kr.co.donghyun.player.data.extractor.network.ExtractorRemote
import kr.co.donghyun.player.data.extractor.repository.ExtractorRepository
import kr.co.donghyun.player.data.extractor.repository.SimpleExtractorRepository

@InstallIn(SingletonComponent::class)
@Module
object ExtractorModule {
    @Provides
    fun provideExtractorRepository(remote : ExtractorRemote, extractorDao: ExtractorDao) : ExtractorRepository {
        return SimpleExtractorRepository(remote, extractorDao)
    }
}