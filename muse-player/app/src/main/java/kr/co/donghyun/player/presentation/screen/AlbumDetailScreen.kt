package kr.co.donghyun.player.presentation.screen

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.co.donghyun.player.data.album.model.Music
import kr.co.donghyun.player.presentation.components.AlbumDetailComponents
import kr.co.donghyun.player.presentation.util.calculateLuminanceFromBitmap
import kr.co.donghyun.player.presentation.viewmodel.AlbumViewModel

@Composable
fun AlbumDetailScreen(viewModel: AlbumViewModel, onClick : (Music, Int) -> Unit) {
    with(viewModel) {
        val album = remember { albumDetail }
        val isPlayingSong = remember { playbackManager.playingStateOfResponse }
        val context = LocalContext.current

        val totalCount = album.value?.totalMusicCount?.toString().orEmpty()
        val totalPlayTime = album.value?.totalPlayTime ?: "0 Minutes"
        var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
        var textColor by remember { mutableStateOf<Color?>(null) }

        MinimumMusicControllerScreen(playbackManager = playbackManager, isPlaying = exoPlayer.isPlaying, paddingValues = PaddingValues(bottom = 48.dp), playingMusic = isPlayingSong.value) {
            if(album.value != null) {
                LazyColumn {
                    item {
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .height(292.dp)
                        ) {
                            AsyncImage(
                                modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop,
                                model = album.value?.albumThumbnail.orEmpty(),
                                contentDescription = "banner image"
                            )
                            Column(modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp), verticalArrangement = Arrangement.Bottom, horizontalAlignment = Alignment.Start) {
                                Text(text = album.value?.albumName.orEmpty(), color = textColor ?: Color.White, fontSize = 32.sp, fontWeight = FontWeight(800))
                                Text(text = "$totalCount Musics, $totalPlayTime", color = textColor ?: Color.White, fontSize = 18.sp)
                            }
                        }
                    }

                    items(album.value?.musics ?: listOf()) { song ->
                        AlbumDetailComponents(albumSong = song, album.value?.artist, onClick = {
                            onClick(song, (album.value?.musics ?: listOf()).indexOf(song))
                        })
                    }

                    item {
                        Column(modifier = Modifier.padding(bottom = 96.dp)) {}
                    }
                }
            }else{
                LazyColumn {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(292.dp)
                                .placeholder(
                                    visible = true,
                                    highlight = PlaceholderHighlight.shimmer()
                                )
                        )
                    }

                    items(10) { // Fake placeholders for 10 songs
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .placeholder(
                                        visible = true,
                                        highlight = PlaceholderHighlight.shimmer()
                                    )
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(64.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.6f)
                                        .height(16.dp)
                                        .placeholder(
                                            visible = true,
                                            highlight = PlaceholderHighlight.shimmer()
                                        )
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.4f)
                                        .height(14.dp)
                                        .placeholder(
                                            visible = true,
                                            highlight = PlaceholderHighlight.shimmer()
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
