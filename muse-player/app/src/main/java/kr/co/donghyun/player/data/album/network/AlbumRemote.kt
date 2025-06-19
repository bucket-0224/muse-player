package kr.co.donghyun.player.data.album.network

import kr.co.donghyun.player.data.album.model.AlbumDetailResponse
import kr.co.donghyun.player.data.util.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface AlbumRemote {
    @GET("channel/album-info")
    suspend fun getAlbumDetail(
        @Query("albumId") albumId : String,
        @Query("channelId") channelId : String
    ) : Response<AlbumDetailResponse>
}