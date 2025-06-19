package kr.co.donghyun.player.presentation.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.IBinder
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.ui.PlayerNotificationManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import dagger.hilt.android.AndroidEntryPoint
import kr.co.donghyun.player.data.album.model.Music
import kr.co.donghyun.player.data.album.model.VideoItem
import kr.co.donghyun.player.presentation.ui.activites.MainActivity
import kr.co.donghyun.player.presentation.ui.activites.PlayerActivity
import kr.co.donghyun.player.presentation.util.PlaybackManager
import kr.co.donghyun.player.presentation.util.Util
import javax.inject.Inject

@UnstableApi
@AndroidEntryPoint
class MusicPlayerService : Service() {

    @Inject
    lateinit var player: ExoPlayer

    @Inject
    lateinit var playbackManager: PlaybackManager

    private lateinit var mediaSession: MediaSession
    private lateinit var notificationManager: PlayerNotificationManager
    private var albumBitmap: Bitmap? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        // 2. MediaSession 설정
        mediaSession = MediaSession.Builder(this, player).build()

        // 3. Notification Manager 설정
        notificationManager = PlayerNotificationManager.Builder(
            this,
            1,
            "media_playback_channel"
        )
            .setMediaDescriptionAdapter(DescriptionAdapter())
            .setNotificationListener(NotificationListener())
            .build()

        notificationManager.setPlayer(player)
        notificationManager.setMediaSessionToken(mediaSession.platformToken)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "ACTION_PLAY" -> {
                with(playbackManager) {
                    val imageUrl = if(playingStateOfResponse.value is Music?) {
                        (playingStateOfResponse.value as Music?)?.thumbnailUrl
                    } else if(playingStateOfResponse.value is VideoItem?) {
                        (playingStateOfResponse.value as VideoItem?)?.thumbnail?.url
                    } else ""

                    setAlbumCover(imageUrl)
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    fun setAlbumCover(imageUrl: String?) {
        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .centerCrop()
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        albumBitmap = resource
                        notificationManager.invalidate()
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
        }
    }

    // 🔹 Notification 설명 어댑터
    private inner class DescriptionAdapter : PlayerNotificationManager.MediaDescriptionAdapter {
        override fun getCurrentContentTitle(player: Player): CharSequence {
            return player.mediaMetadata.title ?: "알 수 없는 제목"
        }

        override fun getCurrentContentText(player: Player): CharSequence? {
            return player.mediaMetadata.artist ?: "알 수 없는 아티스트"
        }

        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback
        ): Bitmap? {
            return albumBitmap
        }

        override fun createCurrentContentIntent(player: Player): PendingIntent? {
            val intent = Intent(this@MusicPlayerService, PlayerActivity::class.java).apply {
                putExtra("isAnotherMusic", false)
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }

            return PendingIntent.getActivity(
                this@MusicPlayerService,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }

    // 🔹 Notification 리스너
    private inner class NotificationListener : PlayerNotificationManager.NotificationListener {
        override fun onNotificationPosted(notificationId: Int, notification: Notification, ongoing: Boolean) {
            startForeground(notificationId, notification)
        }

        override fun onNotificationCancelled(notificationId: Int, dismissed: Boolean) {
            stopForeground(true)
            stopSelf()
        }
    }

    // 🔹 채널 생성
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "media_playback_channel",
            "Media Playback",
            NotificationManager.IMPORTANCE_LOW
        )
        getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
    }

    override fun onDestroy() {
        super.onDestroy()
        notificationManager.setPlayer(null)
        player.release()
        mediaSession.release()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
