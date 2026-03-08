package kr.co.donghyun.player.domain

import kr.co.donghyun.player.data.extractor.repository.FeatureRepository
import okhttp3.MultipartBody
import javax.inject.Inject

class FeatureUseCase @Inject constructor(private val featureRepository: FeatureRepository) {
    suspend fun getFeature(videoId : String) = featureRepository.getFeature(videoId)
    suspend fun uploadCookies(cookie: MultipartBody.Part) = featureRepository.uploadCookies(cookie)
    suspend fun getShortFeatures(keyword : String) = featureRepository.getFeatureShorts(keyword)
}