package kr.co.donghyun.player.data.channel.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kr.co.donghyun.player.data.channel.dao.ChannelDao
import kr.co.donghyun.player.data.channel.network.ChannelRemote
import kr.co.donghyun.player.data.channel.repository.ChannelRepository
import kr.co.donghyun.player.data.channel.repository.SimpleChannelRepository

@InstallIn(SingletonComponent::class)
@Module
object ChannelModule {
    @Provides
    fun provideChannelRepository(remote : ChannelRemote, channelDao: ChannelDao) : ChannelRepository {
        return SimpleChannelRepository(remote, channelDao)
    }
}