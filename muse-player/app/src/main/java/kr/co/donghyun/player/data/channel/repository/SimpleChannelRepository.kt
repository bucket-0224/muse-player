package kr.co.donghyun.player.data.channel.repository

import kr.co.donghyun.player.data.channel.dao.ChannelDao
import kr.co.donghyun.player.data.channel.model.ChannelDataResponse
import kr.co.donghyun.player.data.channel.model.SearchItem
import kr.co.donghyun.player.data.channel.model.SearchResponse
import kr.co.donghyun.player.data.channel.network.ChannelRemote
import retrofit2.Response
import javax.inject.Inject

class SimpleChannelRepository @Inject constructor(
    private val remote : ChannelRemote,
    private val channelDao: ChannelDao
) : ChannelRepository {
    override suspend fun getChannelInfo(channelId: String): Response<ChannelDataResponse> {
        return remote.getChannelsInfo(channelId = channelId)
    }

    override suspend fun searchVideosByQuery(query: String): Response<SearchResponse> {
        return remote.searchVideosByQuery(query)
    }

    override suspend fun insertRecentSearchedChannel(artist: SearchItem) {
        return channelDao.insert(artist)
    }

    override suspend fun fetchAllRecentSearchedChannels(): List<SearchItem> {
        return channelDao.fetchAll()
    }
}