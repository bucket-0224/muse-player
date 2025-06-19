package kr.co.donghyun.player.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kr.co.donghyun.player.data.album.model.Music
import kr.co.donghyun.player.data.extractor.model.ExtractorResponseBody
import kr.co.donghyun.player.presentation.components.MinimumPlayingStateComponents
import kr.co.donghyun.player.presentation.util.PlaybackManager

@Composable
fun MinimumMusicControllerScreen(playbackManager: PlaybackManager, isPlaying : Boolean, paddingValues: PaddingValues = PaddingValues(top = 24.dp), playingMusic: Any?, content : @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        content()

        if (playingMusic != null) {
            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom, horizontalAlignment = Alignment.CenterHorizontally) {
                MinimumPlayingStateComponents(isPlaying = isPlaying, paddingValues = paddingValues, playbackManager = playbackManager, playingMusic = playbackManager.playingStateOfResponse.value)
            }
        }
    }
}