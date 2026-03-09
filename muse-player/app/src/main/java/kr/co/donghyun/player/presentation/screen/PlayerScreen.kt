package kr.co.donghyun.player.presentation.screen

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import kr.co.donghyun.player.data.album.model.Music
import kr.co.donghyun.player.presentation.components.ControllerComponents
import kr.co.donghyun.player.presentation.components.PlayerComponents
import kr.co.donghyun.player.presentation.theme.PlayerTheme
import kr.co.donghyun.player.presentation.ui.activites.PlayerActivity
import kr.co.donghyun.player.presentation.util.Util
import kr.co.donghyun.player.presentation.viewmodel.PlayerViewModel
import androidx.core.net.toUri
import kr.co.donghyun.player.data.channel.model.SearchItem
import kr.co.donghyun.player.presentation.ui.activites.SearchArtistActivity
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    currentMusicPosition: MutableLongState,
    currentMusicSeekBarPosition: MutableFloatState,
    musicDuration: MutableLongState,
    isPlayingState: MutableState<Boolean>,
    playingStateCallback: (Boolean) -> Unit,
    data: Any?,
    isOnPlaylist: Boolean,
    viewModel: PlayerViewModel,
    onNextMusic: () -> Unit,
    onPreviousMusic: () -> Unit,
) {
    val context = LocalContext.current
    var expandedDropDown by remember { mutableStateOf(false) }
    val videoUrl by viewModel.playbackManager.currentPlayingVideoUrl.collectAsState()

    PlayerTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Column(modifier = Modifier
                .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                TopAppBar(title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        IconButton(onClick = {
                            (context as PlayerActivity).finish()
                        }) {
                            Icon(imageVector = Icons.Filled.KeyboardArrowDown, contentDescription = "Arrow Down")
                        }
                        Text(text = "Now Playing", textAlign = TextAlign.Center, modifier = Modifier.weight(1f), fontSize = 16.sp)
                        Box {
                            IconButton(onClick = {
                                expandedDropDown = true
                            }) {
                                Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "Arrow Down")
                            }

                            DropdownMenu(
                                expanded = expandedDropDown,
                                onDismissRequest = { expandedDropDown = false }
                            ) {
                                if(!isOnPlaylist) {
                                    DropdownMenuItem(
                                        text = { Text("플레이리스트에 담기") },
                                        onClick = {
                                            expandedDropDown = false
                                            if(viewModel.playbackManager.playingStateOfResponse.value is Music?) {
                                                viewModel.insertToPlaylist(viewModel.playbackManager.playingStateOfResponse.value as Music?) {
                                                    Toast.makeText(context, "플레이리스트에 해당 앨범이 추가되었습니다.", Toast.LENGTH_LONG).show()
                                                }
                                            } else {
                                                viewModel.insertToPlaylist(viewModel.playbackManager.playingStateOfResponse.value as SearchItem?) {
                                                    Toast.makeText(context, "플레이리스트에 해당 앨범이 추가되었습니다.", Toast.LENGTH_LONG).show()
                                                }
                                            }
                                        }
                                    )
                                }
                                DropdownMenuItem(
                                    text = { Text("음악 다운로드 받기") },
                                    onClick = {
                                        expandedDropDown = false
                                        try {
                                            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

                                            // Create Muse directory if not exists
                                            val museFolder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).absolutePath)

                                            if (!museFolder.exists()) {
                                                museFolder.mkdirs()
                                            }

                                            val uri = videoUrl.toUri()
                                            val request = DownloadManager.Request(uri).apply {
                                                setTitle("음악 다운로드")
                                                setDescription("음악을 다운로드 중입니다..")
                                                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                                setDestinationUri(Uri.fromFile(File(museFolder, "${if(data is Music?) data?.title else if(data is SearchItem?) data.title else "${SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())}"}.mp3")))
                                                setAllowedOverMetered(true)
                                            }

                                            downloadManager.enqueue(request)
                                            Toast.makeText(context, "다운로드를 시작합니다.", Toast.LENGTH_SHORT).show()

                                        } catch (e: Exception) {
                                            Toast.makeText(context, "다운로드 실패: ${e.message}", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                )
                            }
                        }
                    }
                })
                Column(modifier = Modifier
                    .padding(innerPadding)
                    .weight(1f)) {
                    Spacer(modifier = Modifier.weight(4.5f))
                    Card(shape = RoundedCornerShape(16.dp), modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .padding(32.dp), elevation = CardDefaults.elevatedCardElevation(defaultElevation = 16.dp)) {
                        AsyncImage(modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop, model = if(data is Music?) data?.thumbnailUrl else if(data is SearchItem?) data.thumbnailUrl else "", contentDescription = "Album Cover")
                    }
                    Spacer(modifier = Modifier.weight(4f))
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp, start = 32.dp, end = 32.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.Start) {
                        Text(text = if(data is Music?) data?.title.orEmpty() else if(data is SearchItem?) data.title else "", modifier = Modifier.basicMarquee(), overflow = TextOverflow.Ellipsis, maxLines = 1, fontWeight = FontWeight(900), color = Color.White, fontSize = 24.sp)

                        Text(
                            text = if(data is Music?)
                                data?.artists?.first()?.name.orEmpty()
                            else if(data is SearchItem?)
                                data.artist ?: ""
                            else "",
                            modifier = Modifier.basicMarquee().clickable {
                                context.startActivity(Intent(context, SearchArtistActivity::class.java).apply {
//                                    putExtra("channelId",
//                                        if(data is Music?)
//                                            data?.artists?.first()?.channelId
//                                        else if(data is VideoItem?)
//                                            data.channel.id
//                                        else ""
//                                    )
                                })
                            },
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            color = Color.White,
                            fontSize = 18.sp,
                        )
                        Spacer(modifier = Modifier.padding(bottom = 24.dp))
                    }
                    PlayerComponents(onValueChange = {
                        with(viewModel) {
                            exoPlayer.seekTo(it)
                        }
                    }, currentMusicPosition, currentMusicSeekBarPosition, musicDuration)
                    Spacer(modifier = Modifier.weight(0.5f))
                    ControllerComponents(playerState = isPlayingState.value, onStateChangedCallback = {
                        isPlayingState.value = it
                        playingStateCallback(it)
                    }, onPreviousMusic = onPreviousMusic, onNextMusic = onNextMusic)
                    Spacer(modifier = Modifier.height(64.dp))
                }
            }
        }
    }
}