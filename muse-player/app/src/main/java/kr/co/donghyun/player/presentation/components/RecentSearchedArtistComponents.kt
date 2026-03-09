package kr.co.donghyun.player.presentation.components

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import kr.co.donghyun.player.data.channel.model.Album
import kr.co.donghyun.player.data.channel.model.SearchItem
import kr.co.donghyun.player.presentation.ui.activites.AlbumActivity

@Composable
fun RecentSearchedArtistComponents(onClick : (String) -> Unit, artist : SearchItem?, albums : List<Album>) {
    Column(modifier = Modifier.clickable { onClick(artist?.id ?: "") }) {
        Row(modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start) {
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
                Text(text = artist?.artist ?: "", fontSize = 18.sp, overflow = TextOverflow.Ellipsis, maxLines = 1)
                Text(text = "${artist?.subscribers}", fontSize = 14.sp)
            }
        }

        Box(modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color.DarkGray))

        Spacer(modifier = Modifier.padding(bottom = 16.dp))

        LazyRow {
            items(albums) { album ->
                Column(modifier = Modifier.width(128.dp).padding(horizontal = 8.dp)) {
                    Card(
                        modifier = Modifier
                            .height(128.dp)
                            .padding(bottom = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.elevatedCardElevation(
                            defaultElevation = 8.dp
                        )
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            AsyncImage(modifier = Modifier.fillMaxSize(), model = album.thumbnailUrl, contentScale = ContentScale.Crop, contentDescription = "thumbnail")
                        }
                    }
                    Text(text = album.title, fontSize = 14.sp, overflow = TextOverflow.Ellipsis, maxLines = 1, fontWeight = FontWeight(800), modifier = Modifier.padding(end = 8.dp))
                    Text(text = album.type, fontSize = 12.sp, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(end = 8.dp))
                }
            }
        }
    }
}