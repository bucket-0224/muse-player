package kr.co.donghyun.player.presentation.viewmodel

import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.exoplayer.ExoPlayer
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.co.donghyun.player.data.album.model.AlbumDetailResponse
import kr.co.donghyun.player.data.album.model.Music
import kr.co.donghyun.player.data.extractor.model.ExtractorResponseBody
import kr.co.donghyun.player.domain.AlbumUseCase
import kr.co.donghyun.player.domain.ExtractorUseCase
import kr.co.donghyun.player.presentation.base.BaseViewModel
import kr.co.donghyun.player.presentation.util.PlaybackManager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(
    val exoPlayer: ExoPlayer,
    val playbackManager: PlaybackManager,
    private val albumUseCase: AlbumUseCase,
    private val extractorUseCase: ExtractorUseCase
): BaseViewModel() {

    val albumDetail = mutableStateOf<AlbumDetailResponse?>(null)
    var loadMediaCoroutineJob : Job? = null

    fun getAlbumDetail(albumId : String, channelId : String) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                albumUseCase.getAlbumDetail(albumId = albumId, channelId = channelId)
            }

            if(response.isSuccessful) {
                val body = response.body()

                if(body != null) {
                    albumDetail.value = body
                }
            }
        }
    }

    fun insertToPlaylist(music : Music, onInsertedCallback : () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                albumUseCase.insertMusic(music.copy(
                    insertedAt = Date()
                ))
            }

            onInsertedCallback()
        }
    }

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
}