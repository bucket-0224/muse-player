package kr.co.donghyun.player.data.extractor.repository

import kr.co.donghyun.player.data.extractor.model.ExtractorResponseBody
import kr.co.donghyun.player.data.extractor.model.ShortResponse
import okhttp3.MultipartBody
import retrofit2.Response

interface ExtractorRepository {
    suspend fun uploadCookies(cookie: MultipartBody.Part) : Response<Unit>
    suspend fun getExtractorUrl(cookie : MultipartBody.Part, videoId : String) : Response<ExtractorResponseBody>
    suspend fun getFeatureShorts(keyword : String) : Response<ShortResponse>
    suspend fun insertExtractorResponse(extractorResponseBody: ExtractorResponseBody)
    suspend fun fetchExtractorResponses() : List<ExtractorResponseBody>
}