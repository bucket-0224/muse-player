package kr.co.donghyun.player.presentation.util

import android.graphics.Bitmap
import android.graphics.Color
import android.webkit.CookieManager
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import kr.co.donghyun.player.data.album.model.Cookie
import kr.co.donghyun.player.data.util.Constants

object Util {
    enum class MENU {
        HOME,
        PLAYLIST,
        SHORT,
        SETTING
    }

    enum class SEARCH {
        ARTIST,
        VIDEO
    }
}

const val SHARED_PREFERENCES = "PreferencesMusic"
const val COOKIES_ID = "SavedCookies"

fun calculateLuminanceFromBitmap(bitmap: Bitmap): Double {
    var rSum = 0L
    var gSum = 0L
    var bSum = 0L
    val width = bitmap.width
    val height = bitmap.height
    val total = width * height

    val pixels = IntArray(total)
    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

    for (pixel in pixels) {
        val r = Color.red(pixel)
        val g = Color.green(pixel)
        val b = Color.blue(pixel)

        rSum += r
        gSum += g
        bSum += b
    }

    val avgR = rSum / total
    val avgG = gSum / total
    val avgB = bSum / total

    // Luminance 계산 (ITU-R BT.709 기준)
    return (0.2126 * avgR + 0.7152 * avgG + 0.0722 * avgB) / 255.0
}

fun appendCookiesNetscapeFormat(url: String) : String {
    val resultCookies = StringBuilder()
    val cookieManager = CookieManager.getInstance()
    val cookies = cookieManager.getCookie(url)
    if (cookies == null) {
        println("No cookies for $url")
        return ""
    }

    val domain = getDomainFromUrl(url) // 도메인 추출 함수 정의 필요
    val domainField = if (domain.startsWith(".")) domain else ".$domain"
    val domainSpecified = "TRUE"  // 도메인 앞에 점(.) 붙였으므로 TRUE
    val path = "/"               // 일반적으로 "/"
    val secureStr = if (url.startsWith("https://")) "TRUE" else "FALSE"
    val expiration = "9223372036854775807" // 최대 만료시간, 필요시 변경 가능

    // 쿠키 문자열은 "key=value; key2=value2; ..." 형식임
    val cookiePairs = cookies.split(";").map { it.trim() }

    resultCookies.run {
        append("# Netscape HTTP Cookie File\n")
        append("# http://curl.haxx.se/rfc/cookie_spec.html\n")
        append("# This is a generated file!  Do not edit.\n\n")

        for (cookie in cookiePairs) {
            val splitIndex = cookie.indexOf("=")
            if (splitIndex == -1) continue

            val name = cookie.substring(0, splitIndex)
            val value = cookie.substring(splitIndex + 1)

            // Netscape cookie 포맷 출력
            // domain, domain_specified, path, secure, expiration, name, value
            append("$domainField\t$domainSpecified\t$path\t$secureStr\t$expiration\t$name\t$value\n")
        }
    }

    return resultCookies.toString()
}

// 간단 도메인 추출 함수 (예: https://www.youtube.com/watch?v=123 -> .youtube.com)
fun getDomainFromUrl(url: String): String {
    return try {
        val uri = android.net.Uri.parse(url)
        val host = uri.host ?: ""
        // www 제거
        val domain = if (host.startsWith("www.")) host.substring(4) else host
        domain
    } catch (e: Exception) {
        ""
    }
}

fun generateShortYoutubeUrl(videoId : String): String {
    return "${Constants.youtubeBaseUrl}video/stream-shorts?videoId=${videoId}"
}

fun generateYoutubeUrl(videoId : String): String {
    return "${Constants.youtubeBaseUrl}video/stream-features?videoId=${videoId}"
}