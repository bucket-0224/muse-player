package kr.co.donghyun.player.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.co.donghyun.player.data.album.model.Music
import kr.co.donghyun.player.data.channel.model.Album
import kr.co.donghyun.player.data.channel.model.ArtistPreview
import kr.co.donghyun.player.data.album.model.VideoItem
import kr.co.donghyun.player.data.extractor.model.FeatureResponse
import kr.co.donghyun.player.domain.AlbumUseCase
import kr.co.donghyun.player.domain.ChannelUseCase
import kr.co.donghyun.player.domain.FeatureUseCase
import kr.co.donghyun.player.presentation.base.BaseViewModel
import kr.co.donghyun.player.presentation.util.PlaybackManager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val exoPlayer: ExoPlayer,
    val playbackManager: PlaybackManager,
    private val channelUseCase: ChannelUseCase,
    private val albumUseCase: AlbumUseCase,
    private val featureUseCase: FeatureUseCase
) : BaseViewModel() {
    val searchedVideos = mutableStateListOf<VideoItem?>()
    val searchedArtists = mutableStateListOf<ArtistPreview?>()
    val recentlySearchedArtists = mutableStateListOf<ArtistPreview?>()
    val recentlySearchedArtistsAlbums = mutableStateListOf<List<Album>>()
    private val _shortsList = MutableStateFlow<List<String>>(emptyList())
    val shortsList: StateFlow<List<String>> = _shortsList.asStateFlow()

    var loadMediaCoroutineJob : Job? = null
    var searchCoroutineJob : Job? = null

    init {
        fetchMyPlaylist()
    }

    fun fetchShortFeature(videoId : String, onSuccess : (FeatureResponse?) -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                Log.d("TAG", "${featureUseCase.getFeature(videoId).body()}")
                onSuccess(featureUseCase.getFeature(videoId).body())
            }
        }
    }

    fun fetchShortsList() {
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    featureUseCase.getShortFeatures(keyword = "데이먼스").body()?.videoIds ?: listOf()
                }
                _shortsList.value = result // 데이터가 로드되면 상태 업데이트
            } catch (e: Exception) {
                // 에러 처리
            }
        }
    }

    fun fetchMyPlaylist(onRefreshed : () -> Unit = {}) {
        viewModelScope.launch {
            try {
                playbackManager.setUpFetchedPlaylist(mutableListOf<Any>().apply {
                    addAll(mutableListOf<Any>().apply {
                        addAll(albumUseCase.fetchAllMusic())
                        addAll(albumUseCase.fetchAllVideos())
                    })
                    sortWith(Comparator<Any> { a, b ->
                        val comparatorInsertedAt = if(a is Music?) a.insertedAt else if(a is VideoItem?) a.insertedAt else Date()
                        val comparatorTargetInsertedAt = if(b is Music?) b.insertedAt else if(b is VideoItem?) b.insertedAt else Date()

                        comparatorInsertedAt.compareTo(comparatorTargetInsertedAt)
                    })
                    reverse()
                    onRefreshed()
                })
            }catch (ex : Exception) {
                ex.printStackTrace()
            }
        }
    }

    fun insertSearchedArtist(artistPreview: ArtistPreview?) {
        if(artistPreview != null) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    channelUseCase.insertRecentSearchedArtist(artistPreview)
                }
            }
        }
    }

    fun fetchRecentlySearchedArtists() {
        viewModelScope.launch(Dispatchers.IO) {
            recentlySearchedArtists.clear()
            recentlySearchedArtistsAlbums.clear()

            val artists = channelUseCase.fetchAllSearchedArtists()

            artists.forEach { artist ->
                val response = channelUseCase.getChannelInfo(artist.artistId)

                recentlySearchedArtistsAlbums.add(response.body()?.artist?.singles.orEmpty().toMutableList().apply {
                    addAll(response.body()?.artist?.albums.orEmpty())
                })
                recentlySearchedArtists.add(artist)
            }
        }
    }

    fun searchVideos(query : String) {
        if(searchCoroutineJob?.isActive == true) {
            searchCoroutineJob?.cancel()
        }
        searchCoroutineJob = viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    channelUseCase.searchVideos(query)
                }

                if(response.isSuccessful) {
                    searchedVideos.run {
                        clear()
                        addAll(response.body()?.videos?.toList() ?: listOf())
                    }
                }
            }catch (ex : Exception) {
                ex.printStackTrace()
            }
        }
    }

    fun searchChannel(query : String) {
        if(searchCoroutineJob?.isActive == true) {
            searchCoroutineJob?.cancel()
        }

        searchCoroutineJob = viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    channelUseCase.searchChannel(query)
                }

                if(response.isSuccessful) {
                    searchedArtists.run {
                        clear()
                        addAll(response.body()?.artist?.toList() ?: listOf())
                    }
                }
            }catch (ex : Exception) {
                ex.printStackTrace()
            }
        }
    }

}