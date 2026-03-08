package kr.co.donghyun.player.data.extractor.repository

import kr.co.donghyun.player.data.extractor.dao.FeatureDao
import kr.co.donghyun.player.data.extractor.model.FeatureResponse
import kr.co.donghyun.player.data.extractor.model.ShortResponse
import kr.co.donghyun.player.data.extractor.network.FeatureRemote
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject

class SimpleFeatureRepository @Inject constructor(
    private val remote : FeatureRemote,
    private val extractorDao: FeatureDao
) : FeatureRepository {
    override suspend fun uploadCookies(cookie: MultipartBody.Part): Response<Unit> {
        return remote.uploadCookies(cookie)
    }

    override suspend fun getFeature(
        videoId: String
    ): Response<FeatureResponse> {
        return remote.getFeature(videoId = videoId)
    }

    override suspend fun getFeatureShorts(keyword: String): Response<ShortResponse> {
        return remote.getFeatureShorts(keyword = keyword)
    }

    override suspend fun insertFeature(featureResponse: FeatureResponse) {
        return extractorDao.insert(featureResponse)
    }

    override suspend fun fetchFeatures(): List<FeatureResponse> {
       return extractorDao.fetchAll()
    }
}