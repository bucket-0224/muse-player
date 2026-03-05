package kr.co.donghyun.player.data.extractor.repository

import kr.co.donghyun.player.data.extractor.dao.ExtractorDao
import kr.co.donghyun.player.data.extractor.model.ExtractorResponseBody
import kr.co.donghyun.player.data.extractor.network.ExtractorRemote
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject

class SimpleExtractorRepository @Inject constructor(
    private val remote : ExtractorRemote,
    private val extractorDao: ExtractorDao
) : ExtractorRepository {
    override suspend fun uploadCookies(cookie: MultipartBody.Part): Response<Unit> {
        return remote.uploadCookies(cookie)
    }

    override suspend fun getExtractorUrl(
        cookie: MultipartBody.Part,
        videoId: String
    ): Response<ExtractorResponseBody> {
        return remote.getExtractorUrl(cookie = cookie, videoId = videoId)
    }

    override suspend fun insertExtractorResponse(extractorResponseBody: ExtractorResponseBody) {
        return extractorDao.insert(extractorResponseBody)
    }

    override suspend fun fetchExtractorResponses(): List<ExtractorResponseBody> {
       return extractorDao.fetchAll()
    }
}