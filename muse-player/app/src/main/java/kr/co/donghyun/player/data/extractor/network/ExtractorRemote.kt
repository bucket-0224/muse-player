package kr.co.donghyun.player.data.extractor.network

import kr.co.donghyun.player.data.extractor.model.ExtractorResponseBody
import kr.co.donghyun.player.data.util.Constants
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ExtractorRemote {
    @Multipart
    @POST("video/features")
    suspend fun getExtractorUrl(
        @Part cookie : MultipartBody.Part,
        @Query("videoId") videoId : String,
    ) : Response<ExtractorResponseBody>

    @Multipart
    @POST("video/upload-cookie")
    suspend fun uploadCookies(
        @Part cookie : MultipartBody.Part
    ) : Response<Unit>
}