package kr.co.donghyun.player.data.channel.repository

import kr.co.donghyun.player.data.channel.dao.ChannelDao
import kr.co.donghyun.player.data.channel.model.ArtistPreview
import kr.co.donghyun.player.data.channel.model.ChannelDataResponse
import kr.co.donghyun.player.data.channel.model.SearchChannelRequest
import kr.co.donghyun.player.data.channel.model.SearchChannelResponse
import kr.co.donghyun.player.data.album.model.SearchVideoResponse
import kr.co.donghyun.player.data.channel.network.ChannelRemote
import retrofit2.Response
import javax.inject.Inject

class SimpleChannelRepository @Inject constructor(
    private val remote : ChannelRemote,
    private val channelDao: ChannelDao
) : ChannelRepository {
    override suspend fun searchVideos(query: String): Response<SearchVideoResponse> {
        return remote.searchVideos(query)
    }

    override suspend fun getChannelInfo(channelId: String): Response<ChannelDataResponse> {
        return remote.getChannelsInfo(channelId = channelId)
    }

    override suspend fun searchChannel(query: String): Response<SearchChannelResponse> {
        return remote.searchChannel(searchChannelRequest = SearchChannelRequest(query))
    }

    override suspend fun insertRecentSearchedChannel(artistPreview: ArtistPreview) {
        return channelDao.insert(artistPreview)
    }

    override suspend fun fetchAllRecentSearchedChannels(): List<ArtistPreview> {
        return channelDao.fetchAll()
    }
}