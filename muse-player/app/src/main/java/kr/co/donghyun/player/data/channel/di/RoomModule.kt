package kr.co.donghyun.player.data.channel.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kr.co.donghyun.player.data.channel.dao.ChannelDao
import kr.co.donghyun.player.data.channel.database.ChannelDatabase
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RoomModule {
    @Singleton
    @Provides
    fun provideChannelDao(channelDatabase: ChannelDatabase): ChannelDao = channelDatabase.channelDao()

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): ChannelDatabase
            = Room.databaseBuilder(context, ChannelDatabase::class.java, "channel_db")
        .fallbackToDestructiveMigration()
        .build()
}