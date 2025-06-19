package kr.co.donghyun.player.domain

import kr.co.donghyun.player.data.channel.model.ArtistPreview
import kr.co.donghyun.player.data.channel.model.ChannelDataResponse
import kr.co.donghyun.player.data.channel.model.SearchChannelResponse
import kr.co.donghyun.player.data.album.model.SearchVideoResponse
import kr.co.donghyun.player.data.channel.repository.ChannelRepository
import retrofit2.Response
import javax.inject.Inject

class ChannelUseCase @Inject constructor(private val repository: ChannelRepository) {
   suspend fun searchVideos(query : String) : Response<SearchVideoResponse> = repository.searchVideos(query = query)
   suspend fun getChannelInfo(channelId : String) : Response<ChannelDataResponse> = repository.getChannelInfo(channelId = channelId)
   suspend fun searchChannel(query : String) : Response<SearchChannelResponse> = repository.searchChannel(query)
   suspend fun insertRecentSearchedArtist(artistPreview: ArtistPreview) = repository.insertRecentSearchedChannel(artistPreview)
   suspend fun fetchAllSearchedArtists() = repository.fetchAllRecentSearchedChannels()
}