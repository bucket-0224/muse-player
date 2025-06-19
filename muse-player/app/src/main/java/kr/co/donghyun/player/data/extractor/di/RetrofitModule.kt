package kr.co.donghyun.player.data.extractor.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kr.co.donghyun.player.data.extractor.network.ExtractorRemote
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
    fun provideRemote() : ExtractorRemote {
        return Retrofit.Builder().baseUrl(youtubeBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(ExtractorRemote::class.java)
    }
}