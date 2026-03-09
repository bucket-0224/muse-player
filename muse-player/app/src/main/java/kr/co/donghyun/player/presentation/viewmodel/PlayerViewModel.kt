package kr.co.donghyun.player.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.co.donghyun.player.domain.FeatureUseCase
import javax.inject.Inject
import androidx.core.net.toUri
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import kotlinx.coroutines.Job
import kr.co.donghyun.player.data.album.model.Music
import kr.co.donghyun.player.data.channel.model.SearchItem
import kr.co.donghyun.player.domain.AlbumUseCase
import kr.co.donghyun.player.presentation.base.BaseViewModel
import kr.co.donghyun.player.presentation.util.PlaybackManager
import java.util.Date

@HiltViewModel
class PlayerViewModel @Inject constructor(
    val exoPlayer: ExoPlayer,
    val playbackManager: PlaybackManager,
    private val featureUseCase: FeatureUseCase,
    private val albumUseCase: AlbumUseCase,
) : BaseViewModel() {
    val isExtracted = mutableStateOf(false)
    var loadMediaCoroutineJob : Job? = null

    fun extractMusicAndPlaying(videoId : String, videoUrl : String) {
        isExtracted.value = true

        if(loadMediaCoroutineJob?.isActive == true) {
            loadMediaCoroutineJob?.cancel()
        }

        loadMediaCoroutineJob = viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val feature = featureUseCase.getFeature(videoId)

                val response = feature.body()

                Log.d("TAG", "response : ${response.toString()}")

                withContext(Dispatchers.Main) {
                    exoPlayer.run {
                        val mediaItem = MediaItem.Builder()
                            .setMimeType(MimeTypes.APPLICATION_M3U8)
                            .setUri(videoUrl.toUri())
                            .setMediaMetadata(
                                MediaMetadata.Builder()
                                    .setTitle(response?.title ?: "")
                                    .setArtist(response?.artist ?: "")
                                    .build())
                            .build()

                        setMediaItem(mediaItem)
                        prepare()
                        playbackManager.setVideo(videoId, videoUrl)
                    }
                }
            }
        }
    }

    fun insertToPlaylist(videoItem : SearchItem?, onInsertedCallback : () -> Unit) {
        if(videoItem != null) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    albumUseCase.insertVideo(videoItem.copy(
                        insertedAt = Date()
                    ))
                }

                onInsertedCallback()
            }
        }
    }

    fun insertToPlaylist(music : Music?, onInsertedCallback : () -> Unit) {
        if(music != null) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    albumUseCase.insertMusic(music.copy(
                        insertedAt = Date(),
                        artistId = ""
                    ))
                }

                onInsertedCallback()
            }
        }
    }
}