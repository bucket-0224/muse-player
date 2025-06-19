package kr.co.donghyun.player.data.album.repository

import kr.co.donghyun.player.data.album.model.AlbumDetailResponse
import kr.co.donghyun.player.data.album.model.Music
import kr.co.donghyun.player.data.album.model.VideoItem
import retrofit2.Response

interface AlbumRepository {
    suspend fun getAlbumDetail(albumId : String, channelId : String) : Response<AlbumDetailResponse>
    suspend fun insertMusic(music: Music)
    suspend fun insertVideo(video : VideoItem)
    suspend fun fetchAllVideos() : List<VideoItem>
    suspend fun fetchAllMusic() : List<Music>
    suspend fun deleteAllMusic()
}