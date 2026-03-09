package kr.co.donghyun.player.data.channel.network

import kr.co.donghyun.player.data.channel.model.ChannelDataResponse
import kr.co.donghyun.player.data.channel.model.SearchItem
import kr.co.donghyun.player.data.channel.model.SearchResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ChannelRemote {
    @GET("channel/channel-info")
    suspend fun getChannelsInfo(
        @Query("channelId") channelId : String
    ) : Response<ChannelDataResponse>

    @GET("channel/search")
    suspend fun searchVideosByQuery(
        @Query("searchQuery") query : String
    ) : Response<SearchResponse>
}