package kr.co.donghyun.player.data.album.repository

import kr.co.donghyun.player.data.album.dao.MusicDao
import kr.co.donghyun.player.data.album.model.AlbumDetailResponse
import kr.co.donghyun.player.data.album.model.Music
import kr.co.donghyun.player.data.album.model.VideoItem
import kr.co.donghyun.player.data.album.network.AlbumRemote
import retrofit2.Response
import javax.inject.Inject

class SimpleAlbumRepository @Inject constructor(val remote : AlbumRemote, val musicDao: MusicDao) : AlbumRepository {
    override suspend fun getAlbumDetail(albumId: String, channelId : String): Response<AlbumDetailResponse> {
        return remote.getAlbumDetail(albumId = albumId, channelId = channelId)
    }

    override suspend fun insertMusic(music: Music) {
        return musicDao.insert(music)
    }

    override suspend fun insertVideo(video: VideoItem) {
        return musicDao.insert(video)
    }

    override suspend fun fetchAllVideos(): List<VideoItem> {
        return musicDao.fetchAllVideos()
    }

    override suspend fun fetchAllMusic(): List<Music> {
        return musicDao.fetchAll()
    }

    override suspend fun deleteAllMusic() {
        return musicDao.deleteAll()
    }
}