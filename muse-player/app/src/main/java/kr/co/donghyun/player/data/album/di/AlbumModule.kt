package kr.co.donghyun.player.data.album.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kr.co.donghyun.player.data.album.dao.MusicDao
import kr.co.donghyun.player.data.album.network.AlbumRemote
import kr.co.donghyun.player.data.album.repository.AlbumRepository
import kr.co.donghyun.player.data.album.repository.SimpleAlbumRepository
import kr.co.donghyun.player.data.channel.network.ChannelRemote
import kr.co.donghyun.player.data.channel.repository.ChannelRepository
import kr.co.donghyun.player.data.channel.repository.SimpleChannelRepository

@InstallIn(SingletonComponent::class)
@Module
object AlbumModule {
    @Provides
    fun provideAlbumRepository(remote : AlbumRemote, musicDao: MusicDao) : AlbumRepository {
        return SimpleAlbumRepository(remote, musicDao)
    }
}