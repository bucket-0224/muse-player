package kr.co.donghyun.player.data.album.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kr.co.donghyun.player.data.album.dao.MusicDao
import kr.co.donghyun.player.data.album.database.MusicDatabase
import kr.co.donghyun.player.data.channel.dao.ChannelDao
import kr.co.donghyun.player.data.channel.database.ChannelDatabase
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RoomModule {
    @Singleton
    @Provides
    fun provideMusicDao(musicDatabase: MusicDatabase): MusicDao = musicDatabase.musicDao()

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): MusicDatabase
            = Room.databaseBuilder(context, MusicDatabase::class.java, "music_playlist_db")
        .fallbackToDestructiveMigration()
        .build()
}