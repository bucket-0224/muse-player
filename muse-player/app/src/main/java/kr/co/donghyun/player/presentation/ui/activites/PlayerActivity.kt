package kr.co.donghyun.player.presentation.ui.activites

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
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
import kr.co.donghyun.player.presentation.screen.PlayerScreen
import kr.co.donghyun.player.presentation.service.MusicPlayerService
import kr.co.donghyun.player.presentation.util.COOKIES_ID
import kr.co.donghyun.player.presentation.util.SHARED_PREFERENCES
import kr.co.donghyun.player.presentation.util.Util
import kr.co.donghyun.player.presentation.viewmodel.PlayerViewModel
import java.io.File
import androidx.core.content.edit

@AndroidEntryPoint
class PlayerActivity : BaseComponentActivity<PlayerViewModel>() {

    override val viewModel: PlayerViewModel
        get() = ViewModelProvider(this)[PlayerViewModel::class.java]


    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreateLifeCycle() {
        overrideActivityTransition(Activity.OVERRIDE_TRANSITION_OPEN, R.anim.slide_up_animation, R.anim.slide_no_animation)
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onPauseLifeCycle() {
        overrideActivityTransition(Activity.OVERRIDE_TRANSITION_CLOSE, R.anim.slide_no_animation, R.anim.slide_down_animation)
    }

    @Composable
    override fun OnViewCreated() {
        with(viewModel) {
            val rememberGetMusicDetail = remember { playbackManager.playingStateOfResponse }
            val isPlayingState = remember { playbackManager.isPlayingState }
            val currentMusicSeekBarPosition = remember { playbackManager.currentMusicSeekBarPosition }
            val currentMusicPosition = remember { playbackManager.currentMusicPosition }
            val musicDuration = remember { playbackManager.musicDuration }
            val isOnPlaylist = remember { playbackManager.isOnPlaylist }

            PlayerScreen(
                currentMusicPosition = currentMusicPosition,
                currentMusicSeekBarPosition = currentMusicSeekBarPosition,
                musicDuration = musicDuration,
                isPlayingState = isPlayingState,
                data = rememberGetMusicDetail.value,
                isOnPlaylist = isOnPlaylist.value,
                playingStateCallback = {
                    if(it) {
                        exoPlayer.play()
                    }else{
                        exoPlayer.pause()
                    }
                },
                viewModel = this,
                onNextMusic = {
                    if(isOnPlaylist.value) {
                        val nextIndex = if (playbackManager.currentMusicIndex.intValue >= playbackManager.getFetchedPlaylist().lastIndex) {
                            0
                        } else {
                            playbackManager.currentMusicIndex.intValue + 1
                        }

                        playbackManager.currentMusicIndex.intValue = nextIndex

                        val videoId = when (val item = playbackManager.getFetchedPlaylist()[nextIndex]) {
                            is Music -> item.youtubeId
                            is VideoItem -> item.id
                            else -> ""
                        }

                        playbackManager.playingStateOfResponse.value = playbackManager.getFetchedPlaylist()[nextIndex]

                        onLoadMediaItem(
                            videoId,
                            onSuccessCallback = { videoId, videoUrl ->
                                playbackManager.setVideo(videoId, videoUrl)
                            }
                        )
                    } else {
                        val nextIndex = if (playbackManager.currentPlayingVideoMusicIndex.intValue >= playbackManager.getFetchedMusicVideoList().lastIndex) {
                            0
                        } else {
                            playbackManager.currentPlayingVideoMusicIndex.intValue + 1
                        }

                        playbackManager.currentPlayingVideoMusicIndex.intValue = nextIndex

                        val videoId = when (val item = playbackManager.getFetchedMusicVideoList()[nextIndex]) {
                            is Music -> item.youtubeId
                            is VideoItem -> item.id
                            else -> ""
                        }

                        playbackManager.playingStateOfResponse.value = playbackManager.getFetchedMusicVideoList()[nextIndex]

                        onLoadMediaItem(
                            videoId,
                            onSuccessCallback = { videoId, videoUrl ->
                                playbackManager.setVideo(videoId, videoUrl)
                            }
                        )
                    }
                },
                onPreviousMusic = {
                    if(isOnPlaylist.value) {
                        val previousIndex = if (playbackManager.currentMusicIndex.intValue <= 0) {
                            playbackManager.getFetchedPlaylist().lastIndex
                        } else {
                            playbackManager.currentMusicIndex.intValue - 1
                        }

                        playbackManager.currentMusicIndex.intValue = previousIndex

                        val videoId = when (val item = playbackManager.getFetchedPlaylist()[previousIndex]) {
                            is Music -> item.youtubeId
                            is VideoItem -> item.id
                            else -> ""
                        }

                        playbackManager.playingStateOfResponse.value = playbackManager.getFetchedPlaylist()[previousIndex]

                        onLoadMediaItem(
                            videoId,
                            onSuccessCallback = { videoId, videoUrl ->
                                playbackManager.setVideo(videoId, videoUrl)
                            }
                        )
                    } else {
                        val previousIndex = if (playbackManager.currentPlayingVideoMusicIndex.intValue <= 0) {
                            playbackManager.getFetchedMusicVideoList().lastIndex
                        } else {
                            playbackManager.currentPlayingVideoMusicIndex.intValue - 1
                        }

                        playbackManager.currentPlayingVideoMusicIndex.intValue = previousIndex

                        val videoId = when (val item = playbackManager.getFetchedMusicVideoList()[previousIndex]) {
                            is Music -> item.youtubeId
                            is VideoItem -> item.id
                            else -> ""
                        }

                        playbackManager.playingStateOfResponse.value = playbackManager.getFetchedMusicVideoList()[previousIndex]

                        onLoadMediaItem(
                            videoId,
                            onSuccessCallback = { videoId, videoUrl ->
                                playbackManager.setVideo(videoId, videoUrl)
                            }
                        )
                    }
                }
            )
        }
    }

    @OptIn(UnstableApi::class)
    fun onLoadMediaItem(videoId: String? = null, onSuccessCallback : (videoId : String, videoUrl : String) -> Unit) {
        with(viewModel) {
            val videoId = videoId ?: intent.getStringExtra("videoId")
            val isAnotherMusic = playbackManager.currentPlayingVideoId.value != videoId

            if(!videoId.isNullOrBlank() && isAnotherMusic) {
                if(exoPlayer.isPlaying) {
                    exoPlayer.stop()
                }

                playbackManager.currentMusicSeekBarPosition.floatValue = 0F
                playbackManager.currentMusicPosition.longValue = 0L
                playbackManager.musicDuration.longValue = 0L

                val cookies = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE).getString(COOKIES_ID, "").orEmpty()

                val cookiesFile = File(cacheDir, "cookies.txt")
                cookiesFile.bufferedWriter().use { writer ->
                    writer.write(cookies)
                }

                getYoutubeUrlById(
                    cookie = cookiesFile,
                    videoId,
                    onPreparedExoPlayer = { videoUrl ->
                        exoPlayer.run {
                            playWhenReady = true
                            val intent = Intent(this@PlayerActivity, MusicPlayerService::class.java).apply {
                                action = "ACTION_PLAY"
//                                putExtra("channelId", )
                                putExtra("videoId", videoId)
                                putExtra("imageUrl", if(playbackManager.playingStateOfResponse.value is Music?) (playbackManager.playingStateOfResponse.value as Music?)?.thumbnailUrl else (playbackManager.playingStateOfResponse.value as VideoItem?)?.thumbnail?.url)
                            }
                            ContextCompat.startForegroundService(this@PlayerActivity, intent)

                            addListener(object : Player.Listener {
                                override fun onIsPlayingChanged(isPlaying: Boolean) {
                                    playbackManager.isPlayingState.value = isPlaying

                                    CoroutineScope(Dispatchers.Main).launch {
                                        while (isPlaying) {
                                            val duration = exoPlayer.duration.toFloat()
                                            val currentPosition = exoPlayer.currentPosition.toFloat()

                                            playbackManager.currentMusicPosition.longValue = exoPlayer.currentPosition
                                            playbackManager.currentMusicSeekBarPosition.floatValue = (currentPosition / duration).coerceIn(0.0f, 1.0f)

                                            delay(1000L)
                                        }
                                    }

                                    super.onIsPlayingChanged(isPlaying)
                                }
                                override fun onPlaybackStateChanged(playbackState: Int) {
                                    super.onPlaybackStateChanged(playbackState)
                                    playbackManager.musicDuration.longValue = duration
                                }
                            })
                        }
                        onSuccessCallback(videoId, videoUrl)
                    },
                    onError = {
                        getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE).edit {
                            remove(COOKIES_ID)
                            Toast.makeText(this@PlayerActivity, "로그인 세션이 만료되었습니다. 재로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
                            showUpBottomSheet(true)
                        }
                    }
                )
            } else {
                isExtracted.value = playbackManager.playingStateOfResponse.value != null
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }
}