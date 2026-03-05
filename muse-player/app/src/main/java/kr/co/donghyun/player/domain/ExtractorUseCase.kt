package kr.co.donghyun.player.domain

import kr.co.donghyun.player.data.extractor.model.ExtractorResponseBody
import kr.co.donghyun.player.data.extractor.repository.ExtractorRepository
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import javax.inject.Inject

class ExtractorUseCase @Inject constructor(private val extractorRepository: ExtractorRepository) {
    suspend fun getExtractorUrl(cookie : MultipartBody.Part, videoId : String) = extractorRepository.getExtractorUrl(cookie, videoId)
    suspend fun uploadCookies(cookie: MultipartBody.Part) = extractorRepository.uploadCookies(cookie)
}