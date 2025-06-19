package kr.co.donghyun.player.data.channel.repository

import kr.co.donghyun.player.data.channel.model.ArtistPreview
import kr.co.donghyun.player.data.channel.model.ChannelDataResponse
import kr.co.donghyun.player.data.channel.model.SearchChannelResponse
import kr.co.donghyun.player.data.album.model.SearchVideoResponse
import retrofit2.Response

interface ChannelRepository {
    suspend fun searchVideos(query: String) : Response<SearchVideoResponse>
    suspend fun getChannelInfo(channelId : String) : Response<ChannelDataResponse>
    suspend fun searchChannel(query : String) : Response<SearchChannelResponse>
    suspend fun insertRecentSearchedChannel(artistPreview: ArtistPreview)
    suspend fun fetchAllRecentSearchedChannels() : List<ArtistPreview>
}