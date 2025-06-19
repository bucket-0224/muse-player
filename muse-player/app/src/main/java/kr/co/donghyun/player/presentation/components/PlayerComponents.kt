package kr.co.donghyun.player.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableLongState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.galaxygoldfish.waveslider.CircleThumb
import com.galaxygoldfish.waveslider.WaveSlider
import com.galaxygoldfish.waveslider.WaveSliderDefaults
import kr.co.donghyun.player.R
import kr.co.donghyun.player.presentation.theme.PlayerTheme
import kr.co.donghyun.player.presentation.util.formatTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerComponents(onValueChange : (Long) -> Unit, currentMusicValue : MutableLongState, currentMusicSeekbarValue : MutableFloatState, durationValue : MutableLongState) {

    PlayerTheme {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                Text(text = if(durationValue.longValue == 0L) "--:--" else formatTime(currentMusicValue.longValue), modifier = Modifier.padding(start = 32.dp, end = 8.dp))
                Slider(
                    value = currentMusicSeekbarValue.floatValue,
                    onValueChange = {
                        currentMusicSeekbarValue.floatValue = it
                        currentMusicValue.longValue = (durationValue.longValue * it).toLong()

                        onValueChange(currentMusicValue.longValue)
                    },
                    modifier = Modifier.weight(1f),
                    thumb = {
                        Box(
                            modifier = Modifier
                                .size(16.dp) // thumb 크기
                                .background(Color.White, shape = CircleShape)
                                .border(2.dp, Color.Gray, shape = CircleShape)
                        )
                    },
                    track = { sliderPositions ->
                        // 트랙 높이 조정
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(50))
                                .background(Color.LightGray)
                        ) {
                            val progressWidth = sliderPositions.value
                            Box(
                                modifier = Modifier
                                    .width(progressWidth.dp)
                                    .fillMaxHeight()
                                    .background(Color(0xFF4CAF50))
                            )
                        }
                    }
                )
                Text(text = if(durationValue.longValue == 0L) "--:--" else formatTime(durationValue.longValue), modifier = Modifier.padding(start = 8.dp, end = 32.dp))
            }
        }
    }
}

