@file:kotlin.OptIn(ExperimentalMaterial3Api::class)

package kr.co.donghyun.player.presentation.screen

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.co.donghyun.player.data.album.model.Music
import kr.co.donghyun.player.data.album.model.VideoItem
import kr.co.donghyun.player.presentation.components.AlbumDetailComponents
import kr.co.donghyun.player.presentation.components.PlaylistComponents
import kr.co.donghyun.player.presentation.service.MusicPlayerService
import kr.co.donghyun.player.presentation.ui.activites.MainActivity
import kr.co.donghyun.player.presentation.ui.activites.PlayerActivity
import kr.co.donghyun.player.presentation.ui.activites.SearchArtistActivity
import kr.co.donghyun.player.presentation.util.COOKIES_ID
import kr.co.donghyun.player.presentation.util.SHARED_PREFERENCES
import kr.co.donghyun.player.presentation.util.Util
import kr.co.donghyun.player.presentation.viewmodel.MainViewModel
import java.io.File

@OptIn(UnstableApi::class)
@Composable
fun PlaylistScreen(viewModel: MainViewModel, onPlayMusic : (videoId : String, playingType : String, onResult : () -> Unit) -> Unit, innerPadding : PaddingValues) {
    with(viewModel) {
        with(playbackManager) {
            val context = LocalContext.current
            val isClickable = remember { mutableStateOf(true) }
            val isRefreshing = remember { mutableStateOf(false) }
            val stateRefreshing = rememberPullToRefreshState()
            val coroutineScope = rememberCoroutineScope()

            PullToRefreshBox(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(start = 16.dp, end = 16.dp),
                isRefreshing = isRefreshing.value,
                onRefresh = {
                    coroutineScope.launch {
                        stateRefreshing.animateToThreshold()
                        withContext(Dispatchers.Main) {
                            isRefreshing.value = true
                            viewModel.fetchMyPlaylist {
                                isRefreshing.value = false
                            }
                        }
                        stateRefreshing.animateToHidden()
                    }
                },
                state = stateRefreshing
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start
                ) {

                    if (getFetchedPlaylist().isNotEmpty()) {
                        LazyColumn {
                            item {
                                Text(
                                    text = "플레이리스트",
                                    modifier = Modifier.padding(top = 16.dp),
                                    textAlign = TextAlign.Start,
                                    fontWeight = FontWeight(800),
                                    fontSize = 32.sp
                                )
                                Text(
                                    text = "담아둔 플레이리스트에요!",
                                    modifier = Modifier.padding(bottom = 8.dp),
                                    textAlign = TextAlign.Start,
                                    fontWeight = FontWeight(400),
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                            }

                            items(getFetchedPlaylist()) { song ->
                                PlaylistComponents(albumSong = song, onClick = {
                                    if (isClickable.value) {
                                        isClickable.value = false
                                        val videoId =
                                            if (song is Music?) song.youtubeId else if (song is VideoItem?) song.id else ""

                                        playingStateOfResponse.value = song
                                        onPlayMusic(videoId, "") {
                                            context.startActivity(
                                                Intent(
                                                    context,
                                                    PlayerActivity::class.java
                                                ).apply {
                                                    putExtra(
                                                        "isAnotherMusic",
                                                        currentPlayingVideoId.value != videoId
                                                    )
                                                    putExtra("videoId", videoId)

                                                    currentMusicIndex.intValue =
                                                        getFetchedPlaylist().indexOf(song)
                                                    isClickable.value = true
                                                    isOnPlaylist.value = true
                                                    playingStateOfResponse.value = song
                                                })
                                        }
                                    }
                                })
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            item {
                                Spacer(modifier = Modifier.padding(bottom = 96.dp))
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = "플레이리스트",
                                modifier = Modifier.padding(top = 16.dp),
                                textAlign = TextAlign.Start,
                                fontWeight = FontWeight(800),
                                fontSize = 32.sp
                            )
                            Text(
                                text = "담아둔 플레이리스트에요!",
                                modifier = Modifier.padding(bottom = 8.dp),
                                textAlign = TextAlign.Start,
                                fontWeight = FontWeight(400),
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "최근 추가된 플레이리스트가 존재하지 않습니다.",
                                    fontWeight = FontWeight(800),
                                    fontSize = 18.sp
                                )
                                Text(
                                    text = "검색으로 음원을 찾고, 플레이리스트에 추가해보세요!",
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(bottom = 24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.padding(bottom = 96.dp))
                        }
                    }
                }
            }
        }
    }
}