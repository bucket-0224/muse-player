package kr.co.donghyun.player.data.channel.model

import kr.co.donghyun.player.data.common.model.Song

data class ChannelDataResponse(
    val message : String,
    val artist : Artist?
)

data class Artist(
    val artistId: String,
    val name: String,
    val description: String?,
    val albums: List<Album>,
    val singles: List<Album>, // same
    val thumbnails: List<Thumbnail>,
    val suggestedArtists: List<SuggestedArtist>,
    val subscribers: String,
    val songs: List<Song>?
)

data class Album(
    val title: String,
    val type: String,
    val albumId: String,
    val year: String,
    val thumbnailUrl: String,
    val isExplicit: Boolean
)

data class Thumbnail(
    val url: String,
    val width: Int,
    val height: Int
)

data class SuggestedArtist(
    val artistId: String,
    val name: String,
    val subscribers: String,
    val thumbnailUrl: String
)

data class Song(
    val title: String,
    val duration: String?,
    val albumId: String?,
    val isExplicit: Boolean?
)
