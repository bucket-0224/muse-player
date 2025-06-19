package kr.co.donghyun.player.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import kr.co.donghyun.player.data.channel.model.Album

@Composable
fun ArtistsAlbumComponents(albumItem: Album?, onClick: () -> Unit) {
    Column(modifier = Modifier.width(156.dp).padding(horizontal = 8.dp)) {
        Card(
            modifier = Modifier
                .height(156.dp)
                .padding(bottom = 8.dp)
                .clickable { onClick() },
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = 8.dp
            )
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(modifier = Modifier.fillMaxSize(), model = albumItem?.thumbnailUrl, contentScale = ContentScale.Crop, contentDescription = "thumbnail")
            }
        }
        Text(text = albumItem?.title ?: "", overflow = TextOverflow.Ellipsis, maxLines = 1, fontWeight = FontWeight(800), modifier = Modifier.padding(end = 8.dp))
        Text(text = albumItem?.type ?: "", overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(end = 8.dp))
    }
}