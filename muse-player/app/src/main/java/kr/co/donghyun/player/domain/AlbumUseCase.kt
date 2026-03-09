package kr.co.donghyun.player.domain

import kr.co.donghyun.player.data.album.model.Music
import kr.co.donghyun.player.data.album.repository.AlbumRepository
import kr.co.donghyun.player.data.channel.model.SearchItem
import javax.inject.Inject

class AlbumUseCase @Inject constructor(val repository: AlbumRepository) {
    suspend fun getAlbumDetail(albumId : String, channelId : String) = repository.getAlbumDetail(albumId = albumId, channelId = channelId)
    suspend fun insertMusic(music : Music) = repository.insertMusic(music)
    suspend fun fetchAllMusic() = repository.fetchAllMusic()
    suspend fun insertVideo(video : SearchItem) = repository.insertVideo(video)
    suspend fun fetchAllVideos() = repository.fetchAllVideos()
    suspend fun deleteAllMusic() = repository.deleteAllMusic()
}