package kr.co.donghyun.player.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kr.co.donghyun.player.R
import androidx.media3.session.R as MediaR

@Composable
fun ControllerComponents(playerState : Boolean, onStateChangedCallback : (Boolean) -> Unit, onPreviousMusic : () -> Unit, onNextMusic : () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
        Button(onClick = onPreviousMusic, colors = ButtonDefaults.buttonColors().copy(
            containerColor = Color.Transparent,
            contentColor = Color.Transparent,
            disabledContentColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
        )) {
            Image(modifier = Modifier
                .width(56.dp)
                .height(56.dp), colorFilter = ColorFilter.tint(Color.White), painter = painterResource(id = MediaR.drawable.media3_icon_previous), contentDescription = "")
        }
        Spacer(modifier = Modifier.padding(end = 16.dp))
        Button(onClick = { onStateChangedCallback(!playerState) }, colors = ButtonDefaults.buttonColors().copy(
            containerColor = Color.Transparent,
            contentColor = Color.Transparent,
            disabledContentColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
        )) {
            Image(modifier = Modifier
                .width(56.dp)
                .height(56.dp), colorFilter = ColorFilter.tint(Color.White), painter = painterResource(id = if(playerState) R.drawable.ic_baseline_pause_24 else R.drawable.ic_baseline_play_arrow_24), contentDescription = "")
        }
        Spacer(modifier = Modifier.padding(end = 16.dp))
        Button(onClick = onNextMusic, colors = ButtonDefaults.buttonColors().copy(
            containerColor = Color.Transparent,
            contentColor = Color.Transparent,
            disabledContentColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
        )) {
            Image(modifier = Modifier
                .width(56.dp)
                .height(56.dp), colorFilter = ColorFilter.tint(Color.White), painter = painterResource(id = MediaR.drawable.media3_icon_next), contentDescription = "")
        }
    }
}