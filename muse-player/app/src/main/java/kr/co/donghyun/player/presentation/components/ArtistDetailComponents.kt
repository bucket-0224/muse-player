package kr.co.donghyun.player.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import kr.co.donghyun.player.data.channel.model.ArtistPreview
import kr.co.donghyun.player.data.channel.model.SearchChannelResponse
import kr.co.donghyun.player.data.common.model.Song


@Composable
fun ArtistDetailComponents(artist: ArtistPreview?, onClick: (String) -> Unit) {
    Column(modifier = Modifier.clickable { onClick(artist?.artistId ?: "") }) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start) {
            Card(
                shape = RoundedCornerShape(99.dp),
                modifier = Modifier
                    .width(64.dp)
                    .height(64.dp),
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = 8.dp
                )
            ) {
                AsyncImage(model = artist?.thumbnailUrl ?: "", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop, contentDescription = "imageThumbnail")
            }
            Column(modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp), horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Center) {
                Text(text = artist?.name ?: "", fontSize = 18.sp, overflow = TextOverflow.Ellipsis, maxLines = 1)
                Text(text = "${artist?.subscribers}", fontSize = 14.sp)
            }
        }
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color.LightGray))
    }
}