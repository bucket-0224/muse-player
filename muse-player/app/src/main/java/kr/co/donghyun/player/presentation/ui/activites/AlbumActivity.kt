package kr.co.donghyun.player.presentation.ui.activites

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.co.donghyun.player.R
import kr.co.donghyun.player.data.album.model.Music
import kr.co.donghyun.player.data.album.model.VideoItem
import kr.co.donghyun.player.presentation.base.BaseComponentActivity
import kr.co.donghyun.player.presentation.screen.AlbumDetailScreen
import kr.co.donghyun.player.presentation.service.MusicPlayerService
import kr.co.donghyun.player.presentation.theme.PlayerTheme
import kr.co.donghyun.player.presentation.ui.activites.MainActivity
import kr.co.donghyun.player.presentation.util.COOKIES_ID
import kr.co.donghyun.player.presentation.util.SHARED_PREFERENCES
import kr.co.donghyun.player.presentation.util.Util
import kr.co.donghyun.player.presentation.viewmodel.AlbumViewModel
import java.io.File

@AndroidEntryPoint
class AlbumActivity : BaseComponentActivity<AlbumViewModel>() {
    override val viewModel: AlbumViewModel
        get() = ViewModelProvider(this)[AlbumViewModel::class.java]

    override fun onCreateLifeCycle() {}

    override fun onPauseLifeCycle() {}

    @Composable
    override fun OnViewCreated() {
        val isClickable = remember { mutableStateOf(true) }
        val currentAlbum = remember { viewModel.albumDetail }

        AlbumDetailScreen(viewModel = viewModel.apply {
            getAlbumDetail(albumId = intent.getStringExtra("albumId") ?: "", channelId = intent.getStringExtra("channelId") ?: "")
        }, onClick = { song, index ->
            with(viewModel) {
                with(playbackManager) {
                    if(isClickable.value) {
                        isClickable.value = false
                        onLoadMediaItem(song.youtubeId, Util.SEARCH.ARTIST.name) {
                            startActivity(Intent(this@AlbumActivity, PlayerActivity::class.java).apply {
                                playbackManager.run {
                                    isOnPlaylist.value = false
                                    setUpFetchedMusicVideoList(currentAlbum.value?.musics ?: listOf(), index)
                                }
                                putExtra("channelId", intent.getStringExtra("channelId") ?: "")
                                putExtra("isAnotherMusic", song.youtubeId != if(playingStateOfResponse.value is Music?) (playingStateOfResponse.value as Music?)?.youtubeId else (playingStateOfResponse.value as VideoItem?)?.id)
                                putExtra("videoId", song.youtubeId)
                            })
                            isClickable.value = true
                            playingStateOfResponse.value = song
                        }
                    }
                }
            }
        })
    }

    @OptIn(UnstableApi::class)
    fun onLoadMediaItem(videoId: String, playingType : String, successCallback : () -> Unit) {
        with(viewModel) {
            with(playbackManager) {
                if(videoId == playbackManager.currentPlayingVideoId.value) {
                    successCallback()
                } else {
                    if(exoPlayer.isPlaying) {
                        exoPlayer.stop()
                    }

                    currentMusicSeekBarPosition.floatValue = 0F
                    currentMusicPosition.longValue = 0L
                    musicDuration.longValue = 0L

                    val cookies = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE).getString(COOKIES_ID, "").orEmpty()

                    val cookiesFile = File(cacheDir, "cookies.txt")
                    cookiesFile.bufferedWriter().use { writer ->
                        writer.write(cookies)
                    }

                    getYoutubeUrlById(
                        cookie = cookiesFile,
                        videoId,
                        onPreparedExoPlayer = { videoUrl ->
                            val intent = Intent(this@AlbumActivity, MusicPlayerService::class.java).apply {
                                action = "ACTION_PLAY"
                                putExtra("videoId", intent.getStringExtra("videoId"))
                                putExtra("playingType", playingType)
                                putExtra("imageUrl", if(playingStateOfResponse.value is Music?) (playingStateOfResponse.value as Music?)?.thumbnailUrl else (playingStateOfResponse.value as VideoItem?)?.thumbnail?.url)
                            }
                            ContextCompat.startForegroundService(this@AlbumActivity, intent)

                            playbackManager.setVideo(videoId, videoUrl)

                            exoPlayer.run {
                                playWhenReady = true

                                addListener(object : Player.Listener {
                                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                                        isPlayingState.value = isPlaying

                                        CoroutineScope(Dispatchers.Main).launch {
                                            while (isPlaying) {
                                                val duration = exoPlayer.duration.toFloat()
                                                val currentPosition = exoPlayer.currentPosition.toFloat()

                                                currentMusicPosition.longValue = exoPlayer.currentPosition
                                                currentMusicSeekBarPosition.floatValue = (currentPosition / duration).coerceIn(0.0f, 1.0f)

                                                delay(1000L)
                                            }
                                        }

                                        super.onIsPlayingChanged(isPlaying)
                                    }
                                    override fun onPlaybackStateChanged(playbackState: Int) {
                                        super.onPlaybackStateChanged(playbackState)
                                        musicDuration.longValue = duration
                                    }
                                })
                            }

                            successCallback()
                        },
                        onError = {
                            getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE).edit {
                                remove(COOKIES_ID)
                                Toast.makeText(this@AlbumActivity, "로그인 세션이 만료되었습니다. 재로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
                                showUpBottomSheet(true)
                            }
                        }
                    )
                }
            }
        }
    }
}