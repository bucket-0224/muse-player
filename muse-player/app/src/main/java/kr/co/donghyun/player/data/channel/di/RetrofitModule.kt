package kr.co.donghyun.player.data.channel.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kr.co.donghyun.player.data.channel.network.ChannelRemote
import kr.co.donghyun.player.data.util.Constants.youtubeBaseUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@InstallIn(SingletonComponent::class)
@Module
object RetrofitModule {
    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.MINUTES)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    @Provides
    fun provideRemote() : ChannelRemote {
        return Retrofit.Builder().baseUrl(youtubeBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(ChannelRemote::class.java)
    }
}