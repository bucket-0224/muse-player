package kr.co.donghyun.player.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.co.donghyun.player.data.album.model.AlbumDetailResponse
import kr.co.donghyun.player.domain.AlbumUseCase
import kr.co.donghyun.player.domain.FeatureUseCase
import kr.co.donghyun.player.presentation.base.BaseViewModel
import kr.co.donghyun.player.presentation.util.PlaybackManager
import kr.co.donghyun.player.presentation.util.generateYoutubeUrl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(
    val exoPlayer: ExoPlayer,
    val playbackManager: PlaybackManager,
    private val albumUseCase: AlbumUseCase,
    private val featureUseCase: FeatureUseCase
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


    fun getYoutubeUrlById(cookie : File, videoId : String, onPreparedExoPlayer : (String) -> Unit, onError : () -> Unit) {
        if(loadMediaCoroutineJob?.isActive == true) {
            loadMediaCoroutineJob?.cancel()
        }

        loadMediaCoroutineJob = viewModelScope.launch {
            withContext(Dispatchers.Main) {
                exoPlayer.run {

                    val cookiesFile = cookie.asRequestBody("text/plain".toMediaTypeOrNull())
                    val multipartCookie = MultipartBody.Part.createFormData("cookie", cookie.name, cookiesFile)
                    featureUseCase.uploadCookies(multipartCookie)

                    val url = generateYoutubeUrl(videoId)
                    val mediaItem = MediaItem.Builder()
                        .setUri(url.toUri())
                        .build()

                    println("url : ${url}, uri : ${url.toUri()}")

                    setMediaItem(mediaItem)
                    prepare()
                    onPreparedExoPlayer(url)
                }
            }
        }
    }
}