package kr.co.donghyun.player.presentation.ui.activites

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import coil3.compose.AsyncImage
import dagger.hilt.android.AndroidEntryPoint
import kr.co.donghyun.player.R
import kr.co.donghyun.player.presentation.base.BaseComponentActivity
import kr.co.donghyun.player.presentation.components.ArtistsAlbumComponents
import kr.co.donghyun.player.presentation.components.MinimumPlayingStateComponents
import kr.co.donghyun.player.presentation.screen.SearchedArtistScreen
import kr.co.donghyun.player.presentation.viewmodel.SearchArtistViewModel

@AndroidEntryPoint
class SearchArtistActivity : BaseComponentActivity<SearchArtistViewModel>() {
    override val viewModel: SearchArtistViewModel
        get() = ViewModelProvider(this)[SearchArtistViewModel::class.java]

    override fun onCreateLifeCycle() {
        with(viewModel) {
            if(!intent?.getStringExtra("channelId").isNullOrBlank()) {
                getChannelInfo(intent?.getStringExtra("channelId").orEmpty())
            }
        }
    }

    override fun onPauseLifeCycle() { }

    @Composable
    override fun OnViewCreated() {
        val isPlayingSong = remember { viewModel.playbackManager.playingStateOfResponse }

        with(viewModel) {
            Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = {
                MinimumPlayingStateComponents(isPlaying = exoPlayer.isPlaying, paddingValues = PaddingValues(bottom = 48.dp), playbackManager = playbackManager, playingMusic = isPlayingSong.value)
            }) { innerPadding ->
                SearchedArtistScreen(viewModel = viewModel, innerPadding)
            }
        }
    }
}