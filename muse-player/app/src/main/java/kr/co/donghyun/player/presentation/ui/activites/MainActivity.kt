package kr.co.donghyun.player.presentation.ui.activites

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import kr.co.donghyun.player.data.album.model.VideoItem
import kr.co.donghyun.player.presentation.base.BaseComponentActivity
import kr.co.donghyun.player.presentation.components.ArtistDetailComponents
import kr.co.donghyun.player.presentation.components.MinimumPlayingStateComponents
import kr.co.donghyun.player.presentation.components.VideoDetailComponents
import kr.co.donghyun.player.presentation.screen.MinimumMusicControllerScreen
import kr.co.donghyun.player.presentation.screen.PlaylistScreen
import kr.co.donghyun.player.presentation.util.SHARED_PREFERENCES
import kr.co.donghyun.player.presentation.util.Util
import kr.co.donghyun.player.presentation.util.Util.MENU
import kr.co.donghyun.player.presentation.util.appendCookiesNetscapeFormat
import kr.co.donghyun.player.presentation.viewmodel.MainViewModel
import androidx.core.content.edit
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.co.donghyun.player.data.album.model.Music
import kr.co.donghyun.player.presentation.screen.HomeScreen
import kr.co.donghyun.player.presentation.service.MusicPlayerService
import kr.co.donghyun.player.presentation.ui.activites.PlayerActivity
import kr.co.donghyun.player.presentation.util.COOKIES_ID
import java.io.File

@AndroidEntryPoint
class MainActivity : BaseComponentActivity<MainViewModel>() {

    override val viewModel: MainViewModel
        get() = ViewModelProvider(this)[MainViewModel::class.java]

    private val permissionRequestLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allGranted = permissions.entries.all { it.value }

            if (allGranted) {
                Toast.makeText(this, "모든 권한이 전부 처리되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "모든 권한이 필요합니다", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateLifeCycle() {
        checkIsAllPermissionGranted()
        checkIsValidateCookies()
    }

    override fun onPauseLifeCycle() {}

    override fun successLoginCallback() { }

    private fun checkIsValidateCookies() {
        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE)

        viewModel.showUpBottomSheet(sharedPreferences.getString(COOKIES_ID, "").isNullOrEmpty())
    }

    private fun checkIsAllPermissionGranted() {
        val requiredPermissions = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requiredPermissions.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requiredPermissions.add(Manifest.permission.FOREGROUND_SERVICE)
        }


        if (requiredPermissions.isNotEmpty()) {
            permissionRequestLauncher.launch(requiredPermissions.toTypedArray())
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun OnViewCreated() {
        with(viewModel) {
            val isPlayingSong = remember { playbackManager.playingStateOfResponse }
            val currentVideoId by playbackManager.currentPlayingVideoId.collectAsState()
            var selectedItem by remember { mutableIntStateOf(0) }
            val items = listOf(MENU.HOME, MENU.PLAYLIST)
            val selectedIcons = listOf(Icons.Filled.Home, Icons.AutoMirrored.Filled.List)
            val unselectedIcons = listOf(Icons.Outlined.Home, Icons.AutoMirrored.Outlined.List)

            Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = {
                Column {
                    // 플레이어 UI를 BottomBar 위에 추가
                    MinimumPlayingStateComponents(isPlaying = exoPlayer.isPlaying, paddingValues = PaddingValues(top = 0.dp, bottom = 24.dp), playbackManager = playbackManager, playingMusic = isPlayingSong.value)

                    // 네비게이션 바 유지
                    NavigationBar {
                        items.forEachIndexed { index, item ->
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        if (selectedItem == index) selectedIcons[index] else unselectedIcons[index],
                                        contentDescription = item.name
                                    )
                                },
                                label = { Text(item.name) },
                                selected = selectedItem == index,
                                onClick = { selectedItem = index }
                            )
                        }
                    }
                }

            }) { innerPadding ->
                when(selectedItem) {
                    0 -> {
                        HomeScreen(viewModel = this, onPlayMusic = { videoId, playingType, successCallback ->
                            if(videoId != currentVideoId) {
                                onLoadMediaItem(videoId, playingType) { videoId, videoUrl ->
                                    playbackManager.setVideo(videoId, videoUrl)
                                    successCallback()
                                }
                            } else {
                                successCallback()
                            }
                        }, innerPadding)
                    }

                    1 -> {
                        PlaylistScreen(viewModel = this, onPlayMusic = { videoId, playingType, successCallback ->
                            if(videoId != currentVideoId) {
                                onLoadMediaItem(videoId, playingType) { videoId, videoUrl ->
                                    playbackManager.setVideo(videoId, videoUrl)
                                    successCallback()
                                }
                            } else {
                                successCallback()
                            }
                        }, innerPadding)
                    }
                }
            }
        }
    }

    @OptIn(UnstableApi::class)
    fun onLoadMediaItem(videoId: String, playingType : String, successCallback : (videoId : String, videoUrl : String) -> Unit) {
        with(viewModel) {
            Log.d("TAG", "playingId Compare : $videoId , ${playbackManager.currentPlayingVideoId.value}")
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
                    val intent = Intent(this@MainActivity, MusicPlayerService::class.java).apply {
                        action = "ACTION_PLAY"
                        putExtra("videoId", intent.getStringExtra("videoId"))
                        putExtra("playingType", playingType)
                        putExtra("imageUrl",
                            if(playbackManager.playingStateOfResponse.value is Music?)
                                    (playbackManager.playingStateOfResponse.value as Music?)?.thumbnailUrl
                            else
                                    (playbackManager.playingStateOfResponse.value as VideoItem?)?.thumbnail?.url)
                    }
                    ContextCompat.startForegroundService(this@MainActivity, intent)

                    exoPlayer.run {
                        playWhenReady = true

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

                    successCallback(videoId, videoUrl)
                },
                onError = {
                    getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE).edit {
                        remove(COOKIES_ID)
                        Toast.makeText(this@MainActivity, "로그인 세션이 만료되었습니다. 재로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
                        showUpBottomSheet(true)
                    }
                }
            )
        }
    }

    override fun onResume() {
        Log.d("Playlist Latest Played", viewModel.playbackManager.currentPlayingVideoId.value)
        viewModel.fetchRecentlySearchedArtists()
        super.onResume()
    }
}