package kr.co.donghyun.player.presentation.util

import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaybackManager @Inject constructor() {
    val isOnPlaylist = mutableStateOf(false)
    val currentMusicIndex = mutableIntStateOf(0)
    private val _currentPlayingVideoId = MutableStateFlow("")
    private val _currentPlayingVideoUrl = MutableStateFlow("")
    val currentPlayingVideoId: StateFlow<String> = _currentPlayingVideoId.asStateFlow()
    val currentPlayingVideoUrl: StateFlow<String> = _currentPlayingVideoUrl.asStateFlow()
    private val _fetchedPlayList = MutableStateFlow(mutableListOf<Any>())
    val fetchedPlayList = _fetchedPlayList.asStateFlow()
    val playingStateOfResponse = mutableStateOf<Any?>(null)
    val isPlayingState = mutableStateOf(false)
    val currentMusicSeekBarPosition = mutableFloatStateOf(0F)
    val currentMusicPosition = mutableLongStateOf(0L)
    val musicDuration = mutableLongStateOf(0L)

    fun resetPlaybackState() {
        currentMusicSeekBarPosition.floatValue = 0f
        currentMusicPosition.longValue = 0L
        musicDuration.longValue = 0L
    }

    fun setCurrentIndex(index: Int) {
        currentMusicIndex.intValue = index
        playingStateOfResponse.value = _fetchedPlayList.value.getOrNull(index)
    }

    fun getFetchedPlaylist() = fetchedPlayList.value

    fun setUpFetchedPlaylist(list: List<Any>) {
        _fetchedPlayList.value.run {
            clear()
            addAll(list)
        }
    }

    fun setVideo(videoId: String, url: String) {
        _currentPlayingVideoId.value = videoId
        _currentPlayingVideoUrl.value = url
    }
}