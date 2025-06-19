package kr.co.donghyun.player.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import kr.co.donghyun.player.data.album.model.Artist
import kr.co.donghyun.player.data.album.model.Music
import kr.co.donghyun.player.data.album.model.VideoItem


@Composable
fun PlaylistComponents(albumSong : Any?, onClick: () -> Unit) {
    Column(modifier = Modifier.clickable { onClick() }) {
        Row(modifier = Modifier.padding(bottom = 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start) {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .width(64.dp)
                    .height(64.dp),
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = 8.dp
                )
            ) {
                AsyncImage(model = if(albumSong is Music?) albumSong?.thumbnailUrl.orEmpty() else if(albumSong is VideoItem?) albumSong.thumbnail.url else "", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop, contentDescription = "imageThumbnail")
            }
            Column(modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp), horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Center) {
                Text(text = if(albumSong is Music?) albumSong?.title.orEmpty() else if(albumSong is VideoItem?) albumSong.title else "", modifier = Modifier.basicMarquee(), overflow = TextOverflow.Ellipsis, fontSize = 18.sp, maxLines = 1)
                Text(text = "${if(albumSong is Music?) albumSong?.artists?.first()?.name.orEmpty() else if(albumSong is VideoItem?) albumSong.channel.name else ""}, ${if(albumSong is Music?) albumSong?.duration?.label else if(albumSong is VideoItem?) albumSong.durationFormatted else ""}", modifier = Modifier.basicMarquee(), overflow = TextOverflow.Ellipsis, fontSize = 14.sp)
            }
        }
    }
}