package kr.co.donghyun.player.presentation.screen

import android.view.ViewGroup
import androidx.compose.runtime.getValue
import android.widget.FrameLayout
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
import androidx.compose.runtime.remember
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
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import dagger.hilt.android.UnstableApi
import kr.co.donghyun.player.presentation.util.generateShortYoutubeUrl
import kr.co.donghyun.player.presentation.util.generateYoutubeUrl
import kr.co.donghyun.player.presentation.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShortsScreen(viewModel: MainViewModel) {
    // 1. 페이저 상태 설정 (무한 스크롤 효과를 위해 데이터 리스트 준비)
    val pagerState = rememberPagerState(pageCount = { 10 })
    val videoIds by viewModel.shortsList.collectAsState()

    // 화면 진입 시 데이터 페칭 시작
    LaunchedEffect(Unit) {
        viewModel.fetchShortsList()
    }

    VerticalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
        key = { it } // 각 페이지의 고유 키
    ) { page ->
        Box(modifier = Modifier.fillMaxSize()) {
            // 배경: 실제 앱에서는 여기에 ExoPlayer 등으로 비디오를 넣습니다.
            if (videoIds.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    // 로딩 인디케이터나 빈 화면 메시지
                    CircularProgressIndicator(color = Color.White)
                }
            } else {
                // 2. 데이터가 있을 때만 Pager를 실행
                VerticalPager(
                    state = rememberPagerState(pageCount = { videoIds.size }),
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    // 여기서 videoIds[page]에 접근하는 것은 안전합니다.
                    val currentVideoId = videoIds[page]

                    Box(modifier = Modifier.fillMaxSize()) {
                        VideoPlayer(
                            videoUrl = generateShortYoutubeUrl(currentVideoId),
                            isCurrentPage = pagerState.currentPage == page
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 108.dp)
            ) {
                // 하단 정보 레이아웃 (아이디, 설명 등)
                VideoInfoSection(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth(0.8f),
                    page = page
                )
            }
        }
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
private fun VideoPlayer(videoUrl: String, isCurrentPage: Boolean) {
    val context = LocalContext.current

    // ExoPlayer 초기화
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ONE // 무한 반복
            setMediaItem(MediaItem.fromUri(videoUrl))
            prepare()
        }
    }

    // 페이지 변경 시 재생/정지 제어
    LaunchedEffect(isCurrentPage) {
        if (isCurrentPage) {
            exoPlayer.playWhenReady = true
        } else {
            exoPlayer.pause()
        }
    }

    // 화면에서 사라질 때 리소스 해제
    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }

    // 영상을 꽉 채우는 AndroidView
    AndroidView(
        factory = {
            PlayerView(context).apply {
                player = exoPlayer
                useController = false // 기본 컨트롤러 숨김
                // 핵심: 영상을 화면에 꽉 차게 확대 (Crop 스타일)
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
fun VideoInfoSection(modifier: Modifier = Modifier, page: Int) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("@creator_name_$page", color = Color.White, fontWeight = FontWeight.Bold)
        Text(
            "이것은 Jetpack Compose로 만든 쇼츠 UI 샘플입니다. #Compose #Android #Shorts",
            color = Color.White,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}