package kr.co.donghyun.player.data.album.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kr.co.donghyun.player.data.common.model.Song
import java.io.Serializable
import java.util.Date

data class AlbumDetailResponse(
    val message: String,
    val artist: Artist,
    val albumThumbnail : String,
    val albumName : String,
    val totalMusicCount: Int,
    val totalPlayTime: String,
    val musics: List<Music>
)

data class Artist(
    val name: String,
    val thumbnails: List<Thumbnail>,
    val subscribers: String,
)

data class Thumbnail(
    val url: String,
    val width: Int,
    val height: Int
)

@Entity
data class Music(
    val artists: List<ArtistName>,
    val title: String?,
    @PrimaryKey
    val youtubeId : String,
    val duration: Duration,
    val isExplicit: Boolean,
    val album: String,
    val thumbnailUrl: String,
    var insertedAt : Date = Date()
) : Serializable

data class ArtistName(
    val name: String?
)

data class Duration(
    val label: String,
    val totalSeconds: Int
)