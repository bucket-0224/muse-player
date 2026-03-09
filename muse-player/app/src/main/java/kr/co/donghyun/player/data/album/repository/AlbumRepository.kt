package kr.co.donghyun.player.data.album.repository

import kr.co.donghyun.player.data.album.model.AlbumDetailResponse
import kr.co.donghyun.player.data.album.model.Music
import kr.co.donghyun.player.data.channel.model.SearchItem
import retrofit2.Response

interface AlbumRepository {
    suspend fun getAlbumDetail(albumId : String, channelId : String) : Response<AlbumDetailResponse>
    suspend fun insertMusic(music: Music)
    suspend fun insertVideo(video : SearchItem)
    suspend fun fetchAllVideos() : List<SearchItem>
    suspend fun fetchAllMusic() : List<Music>
    suspend fun deleteAllMusic()
}