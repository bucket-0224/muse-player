package kr.co.donghyun.player.data.extractor.network

import kr.co.donghyun.player.data.extractor.model.FeatureResponse
import kr.co.donghyun.player.data.extractor.model.ShortResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface FeatureRemote {
    @GET("video/features")
    suspend fun getFeature(
        @Query("videoId") videoId : String,
    ) : Response<FeatureResponse>

    @GET("video/feature-shorts")
    suspend fun getFeatureShorts(
        @Query("keyword") keyword : String,
    ) : Response<ShortResponse>

    @Multipart
    @POST("video/upload-cookie")
    suspend fun uploadCookies(
        @Part cookie : MultipartBody.Part
    ) : Response<Unit>
}