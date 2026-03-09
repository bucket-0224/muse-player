package kr.co.donghyun.player.presentation.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.co.donghyun.player.data.channel.model.Album
import kr.co.donghyun.player.data.channel.model.Artist
import kr.co.donghyun.player.data.channel.model.SearchItem
import kr.co.donghyun.player.domain.ChannelUseCase
import kr.co.donghyun.player.presentation.base.BaseViewModel
import kr.co.donghyun.player.presentation.util.PlaybackManager
import javax.inject.Inject

@HiltViewModel
class SearchArtistViewModel @Inject constructor(
    val exoPlayer: ExoPlayer,
    val playbackManager: PlaybackManager,
    private val channelUseCase: ChannelUseCase
) : BaseViewModel() {
    val channelInfo = mutableStateOf<Artist?>(null)
    val singles = mutableStateListOf<Album?>()
    val albums = mutableStateListOf<Album?>()


    fun insertSearchedArtist(artistPreview: SearchItem?) {
        if(artistPreview != null) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    channelUseCase.insertRecentSearchedArtist(artistPreview)
                }
            }
        }
    }


    fun getChannelInfo(channelId : String) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    channelUseCase.getChannelInfo(channelId)
                }

                if(response.isSuccessful) {
                    val body = response.body()

                    if(body != null) {
                        channelInfo.value = body.artist
                        singles.run {
                            clear()
                            addAll(body.artist?.singles ?: listOf())
                        }
                        albums.run {
                            clear()
                            addAll(body.artist?.albums ?: listOf())
                        }
                    }
                }
            }catch (ex : Exception) {
                ex.printStackTrace()
            }
        }
    }
}