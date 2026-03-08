package kr.co.donghyun.player.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import kr.co.donghyun.player.R
import kr.co.donghyun.player.data.extractor.model.FeatureResponse
import kr.co.donghyun.player.presentation.theme.PlayerTheme

@Composable
fun AlbumComponents(data : FeatureResponse?) {
    PlayerTheme {
        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.padding(bottom = 32.dp), elevation = CardDefaults.elevatedCardElevation(defaultElevation = 16.dp)) {
                AsyncImage(modifier = Modifier.size(356.dp), contentScale = ContentScale.Crop, model = data?.thumbnail ?: "", contentDescription = "Album Cover")
            }
        }
    }
}