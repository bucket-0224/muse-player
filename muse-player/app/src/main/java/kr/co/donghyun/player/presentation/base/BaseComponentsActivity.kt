package kr.co.donghyun.player.presentation.base

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.edit
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModel
import kr.co.donghyun.player.presentation.theme.PlayerTheme
import kr.co.donghyun.player.presentation.util.COOKIES_ID
import kr.co.donghyun.player.presentation.util.SHARED_PREFERENCES
import kr.co.donghyun.player.presentation.util.appendCookiesNetscapeFormat

abstract class BaseComponentActivity<VM : BaseViewModel> : ComponentActivity() {

    abstract val viewModel : VM


    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onCreateLifeCycle()
        enableEdgeToEdge()
        setContent {
            PlayerTheme {
                ChangeSystemBarsTheme(false)
                OnViewCreated()
                ShowGoogleLoginSessionScreen()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ShowGoogleLoginSessionScreen() {
        val modalBottomSheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
            confirmValueChange = { true }
        )
        var showBottomSheet by remember { viewModel.showUpStatus }


        LaunchedEffect(showBottomSheet) {
            if (showBottomSheet && !modalBottomSheetState.isVisible) {
                modalBottomSheetState.show()
            } else if (!showBottomSheet && modalBottomSheetState.isVisible) {
                modalBottomSheetState.hide()
            }
        }

        if(showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                modifier = Modifier.padding(top = 64.dp),
                sheetState = modalBottomSheetState
            ) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { context ->
                        WebView(context).apply {
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            settings.cacheMode = WebSettings.LOAD_DEFAULT

                            val cookieManager = CookieManager.getInstance()
                            cookieManager.setAcceptCookie(true)
                            cookieManager.setAcceptThirdPartyCookies(this, true)

                            webViewClient = object : WebViewClient() {
                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                    if (url != null && url.contains("youtube.com")) {
                                        val cookies = appendCookiesNetscapeFormat("https://www.youtube.com")
                                        if(cookies.isNotEmpty()) {
                                            getSharedPreferences(
                                                SHARED_PREFERENCES,
                                                MODE_PRIVATE
                                            ).edit {
                                                putString(
                                                    COOKIES_ID,
                                                    cookies
                                                )
                                            }
                                            viewModel.showUpBottomSheet(false)
                                            successLoginCallback()
                                        }
                                    }
                                }
                            }

                            loadUrl("https://accounts.google.com/ServiceLogin?service=youtube")
                        }
                    }
                )
            }
        }
    }

    @Composable
    private fun ChangeSystemBarsTheme(lightTheme: Boolean) {
        val barColor = MaterialTheme.colorScheme.background.toArgb()
        LaunchedEffect(lightTheme) {
            if (lightTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.light(
                        barColor, barColor,
                    ),
                    navigationBarStyle = SystemBarStyle.light(
                        barColor, barColor,
                    ),
                )
            } else {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.dark(
                        barColor,
                    ),
                    navigationBarStyle = SystemBarStyle.dark(
                        barColor,
                    ),
                )
            }
        }
    }

    open fun successLoginCallback() { }

    override fun onPause() {
        super.onPause()
        onPauseLifeCycle()
    }

    abstract fun onCreateLifeCycle()
    abstract fun onPauseLifeCycle()

    @Composable
    abstract fun OnViewCreated()
}