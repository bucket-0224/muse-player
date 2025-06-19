package kr.co.donghyun.player.presentation.base

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {
    val showUpStatus = mutableStateOf(false)

    fun showUpBottomSheet(show : Boolean) {
        showUpStatus.value = show
    }
}