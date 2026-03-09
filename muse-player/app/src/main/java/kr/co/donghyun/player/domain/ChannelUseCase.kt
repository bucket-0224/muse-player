package kr.co.donghyun.player.domain

import kr.co.donghyun.player.data.channel.model.ChannelDataResponse
import kr.co.donghyun.player.data.channel.model.SearchItem
import kr.co.donghyun.player.data.channel.model.SearchResponse
import kr.co.donghyun.player.data.channel.repository.ChannelRepository
import retrofit2.Response
import javax.inject.Inject

class ChannelUseCase @Inject constructor(private val repository: ChannelRepository) {
   suspend fun getChannelInfo(channelId : String) : Response<ChannelDataResponse> = repository.getChannelInfo(channelId = channelId)
   suspend fun searchVideosByQuery(query : String) : Response<SearchResponse> = repository.searchVideosByQuery(query)
   suspend fun insertRecentSearchedArtist(artist: SearchItem) = repository.insertRecentSearchedChannel(artist)
   suspend fun fetchAllSearchedArtists() = repository.fetchAllRecentSearchedChannels()
}