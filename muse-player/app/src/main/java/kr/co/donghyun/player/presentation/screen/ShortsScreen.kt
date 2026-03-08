package kr.co.donghyun.player.presentation.screen

import android.view.ViewGroup
import androidx.compose.runtime.getValue
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import dagger.hilt.android.UnstableApi
import kr.co.donghyun.player.presentation.util.generateShortYoutubeUrl
import kr.co.donghyun.player.presentation.util.generateYoutubeUrl
import kr.co.donghyun.player.presentation.viewmodel.MainViewModel

@Composable
fun ShortsScreen(viewModel: MainViewModel) {
    val videoIds by viewModel.shortsList.collectAsState()

    // 화면 진입 시 데이터 페칭 시작
    LaunchedEffect(Unit) {
        viewModel.fetchShortsList()
    }

    // 1. 데이터가 없을 때는 로딩 화면만 띄웁니다.
    if (videoIds.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
        }
    } else {
        // 2. 데이터가 로드되면 "단 하나"의 Pager만 만듭니다.
        val pagerState = rememberPagerState(pageCount = { videoIds.size })

        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            key = { page -> videoIds[page] } // 각 영상의 고유 ID를 키로 사용하여 성능 향상
        ) { page ->
            val currentVideoId = videoIds[page]

            // 💡 핵심: 현재 Pager의 상태와 현재 그려지는 page를 비교하여 재생 여부 결정
            val isCurrentPage = pagerState.currentPage == page

            Box(modifier = Modifier.fillMaxSize()) {
                // 비디오 플레이어
                VideoPlayer(
                    videoUrl = generateShortYoutubeUrl(currentVideoId),
                    isCurrentPage = isCurrentPage
                )

                // 하단 정보 레이아웃
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 108.dp)
                ) {
                    VideoInfoSection(
                        viewModel = viewModel,
                        videoId = videoIds[page],
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth(0.8f),
                        page = page
                    )
                }
            }
        }
    }
}


@OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
private fun VideoPlayer(videoUrl: String, isCurrentPage: Boolean) {
    val context = LocalContext.current

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ONE // 무한 반복
            // 💡 백엔드에서 MP4를 주므로, M3U8 강제 지정을 제거하고 URL만 넘깁니다.
            setMediaItem(
                MediaItem.Builder()
                    .setUri(videoUrl.toUri())
                    .setMimeType(MimeTypes.APPLICATION_M3U8)
                    .build()
            )
            prepare()
        }
    }

    // 💡 스와이프해서 현재 페이지가 되면 재생, 벗어나면 일시정지
    LaunchedEffect(isCurrentPage) {
        if (isCurrentPage) {
            exoPlayer.play()
        } else {
            exoPlayer.pause()
            // 원한다면 여기서 exoPlayer.seekTo(0)을 호출해 영상을 처음으로 되돌릴 수도 있습니다.
        }
    }

    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }

    AndroidView(
        factory = {
            PlayerView(context).apply {
                player = exoPlayer
                useController = false
                // 💡 쇼츠/틱톡처럼 영상을 화면에 꽉 채우는 핵심 설정
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun VideoInfoSection(viewModel: MainViewModel, modifier: Modifier = Modifier, page: Int, videoId: String) {
    // 💡 by를 사용하면 .value를 생략할 수 있어 코드가 깔끔해집니다.
    var featureChannel by remember { mutableStateOf("") }
    var featureTitle by remember { mutableStateOf("") }

    // 💡 핵심: Unit 대신 videoId를 넣어줍니다!
    LaunchedEffect(videoId) {
        viewModel.fetchShortFeature(videoId) { response ->
            featureChannel = response?.artist ?: ""
            featureTitle = response?.title ?: ""
        }
    }

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // .value 없이 바로 사용 가능
        Text(featureChannel, color = Color.White, fontWeight = FontWeight.Bold)
        Text(
            featureTitle,
            color = Color.White,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}