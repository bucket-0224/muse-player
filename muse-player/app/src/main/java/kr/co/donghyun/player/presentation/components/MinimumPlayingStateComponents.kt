package kr.co.donghyun.player.presentation.components

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import kr.co.donghyun.player.data.album.model.Music
import kr.co.donghyun.player.data.channel.model.SearchItem
import kr.co.donghyun.player.presentation.ui.activites.PlayerActivity
import kr.co.donghyun.player.presentation.util.PlaybackManager

@Composable
fun MinimumPlayingStateComponents(playbackManager: PlaybackManager, paddingValues: PaddingValues, isPlaying : Boolean, playingMusic: Any?) {
    val context = LocalContext.current

    if(playingMusic != null) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 8.dp + paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding()
                )
                .height(72.dp)
                .clickable {
                    context.startActivity(Intent(context, PlayerActivity::class.java).apply {
                        putExtra("isNewPlaying", false)
                    })
                },
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = 4.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF0A0A1E)
            )
        ) {
            Column {
                Row(modifier = Modifier.weight(1f).padding(12.dp), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
                    Card(modifier = Modifier
                        .size(56.dp)
                    ) {
                        AsyncImage(model = if(playingMusic is Music?) playingMusic.thumbnailUrl else if(playingMusic is SearchItem?) playingMusic.thumbnailUrl else "", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop, contentDescription = "imageThumbnail")
                    }
                    Spacer(modifier = Modifier.padding(end = 8.dp))
                    Column {
                        Text(text = if(playingMusic is Music?) playingMusic.title ?: "" else if(playingMusic is SearchItem?) playingMusic.title else "", color = Color.White, modifier = Modifier.basicMarquee(), overflow = TextOverflow.Ellipsis, fontSize = 16.sp, maxLines = 1, fontWeight = FontWeight(600))
                        Text(text = if(playingMusic is Music?) playingMusic.artists.first().name ?: "" else if(playingMusic is SearchItem?) playingMusic.artist ?: "" else "", color = Color.White, modifier = Modifier.basicMarquee(), overflow = TextOverflow.Ellipsis, maxLines = 1, fontSize = 12.sp)
                    }
                }
                Box(
                    modifier = Modifier
                        .height(1.dp)
                        .fillMaxWidth(playbackManager.currentMusicSeekBarPosition.floatValue.coerceIn(0f, 1f)) // 진행 비율만큼 너비 설정
                        .background(Color.White) // 옅은 회색
                )
            }
        }
    }
}