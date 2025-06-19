package kr.co.donghyun.player.presentation.util

import java.util.Locale

fun formatTime(milliseconds: Long): String {
    var seconds = milliseconds / 1000
    val minutes = seconds / 60
    seconds %= 60

    return String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds)
}