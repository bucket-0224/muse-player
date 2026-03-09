package kr.co.donghyun.player.data.channel.repository

import androidx.room.Query
import kr.co.donghyun.player.data.channel.model.ChannelDataResponse
import kr.co.donghyun.player.data.channel.model.SearchItem
import kr.co.donghyun.player.data.channel.model.SearchResponse
import retrofit2.Response

interface ChannelRepository {
    suspend fun getChannelInfo(channelId : String) : Response<ChannelDataResponse>
    suspend fun searchVideosByQuery(query: String) : Response<SearchResponse>
    suspend fun insertRecentSearchedChannel(artistPreview: SearchItem)
    suspend fun fetchAllRecentSearchedChannels() : List<SearchItem>
}