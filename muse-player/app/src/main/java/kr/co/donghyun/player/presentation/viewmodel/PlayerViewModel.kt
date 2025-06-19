package kr.co.donghyun.player.presentation.viewmodel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.co.donghyun.player.data.extractor.model.ExtractorResponseBody
import kr.co.donghyun.player.domain.ExtractorUseCase
import javax.inject.Inject
import androidx.core.net.toUri
import androidx.media3.common.MediaMetadata
import kotlinx.coroutines.Job
import kr.co.donghyun.player.data.album.model.Music
import kr.co.donghyun.player.data.album.model.VideoItem
import kr.co.donghyun.player.domain.AlbumUseCase
import kr.co.donghyun.player.presentation.base.BaseViewModel
import kr.co.donghyun.player.presentation.util.PlaybackManager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.Date

@HiltViewModel
class PlayerViewModel @Inject constructor(
    val exoPlayer: ExoPlayer,
    val playbackManager: PlaybackManager,
    private val extractorUseCase: ExtractorUseCase,
    private val albumUseCase: AlbumUseCase,
) : BaseViewModel() {
    val isExtracted = mutableStateOf(false)
    var loadMediaCoroutineJob : Job? = null

    fun getYoutubeUrlById(cookie : File, videoId : String, onPreparedExoPlayer : (String) -> Unit, onError : () -> Unit) {
        if(loadMediaCoroutineJob?.isActive == true) {
            loadMediaCoroutineJob?.cancel()
        }

        loadMediaCoroutineJob = viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val cookiesFile = cookie.asRequestBody("text/plain".toMediaTypeOrNull())
                val multipartCookie = MultipartBody.Part.createFormData("cookie", cookie.name, cookiesFile)
                val response = extractorUseCase.getExtractorUrl(multipartCookie, videoId)

                if(response.isSuccessful) {
                    val body = response.body()

                    if (body != null) {
                        withContext(Dispatchers.Main) {
                            isExtracted.value = true

                            exoPlayer.run {
                                val mediaItem = if(body.url != null) {
                                    MediaItem.Builder()
                                        .setUri(body.url.toUri())
                                        .setMediaMetadata(
                                            MediaMetadata.Builder()
                                                .setTitle(body.title)
                                                .setArtist(body.artist)
                                                .build()
                                        )
                                        .build()
                                } else null

                                if(mediaItem != null) {
                                    setMediaItem(mediaItem)
                                    prepare()
                                    playbackManager.setVideo(body.videoId, body.url ?: "")
                                    onPreparedExoPlayer(body.url ?: "")
                                }
                            }
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Log.d("TAG", "error : ${response.errorBody()?.string()}")
                            onError()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Log.d("TAG", "error : ${response.errorBody()?.string()}")
                        onError()
                    }
                }
            }
        }
    }

    fun insertToPlaylist(videoItem : VideoItem?, onInsertedCallback : () -> Unit) {
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
                        insertedAt = Date()
                    ))
                }

                onInsertedCallback()
            }
        }
    }
}