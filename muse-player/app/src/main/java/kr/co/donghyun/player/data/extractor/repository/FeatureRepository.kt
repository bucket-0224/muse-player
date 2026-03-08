package kr.co.donghyun.player.data.extractor.repository

import kr.co.donghyun.player.data.extractor.model.FeatureResponse
import kr.co.donghyun.player.data.extractor.model.ShortResponse
import okhttp3.MultipartBody
import retrofit2.Response

interface FeatureRepository {
    suspend fun uploadCookies(cookie: MultipartBody.Part) : Response<Unit>
    suspend fun getFeature(videoId : String) : Response<FeatureResponse>
    suspend fun getFeatureShorts(keyword : String) : Response<ShortResponse>
    suspend fun insertFeature(featureResponse: FeatureResponse)
    suspend fun fetchFeatures() : List<FeatureResponse>
}