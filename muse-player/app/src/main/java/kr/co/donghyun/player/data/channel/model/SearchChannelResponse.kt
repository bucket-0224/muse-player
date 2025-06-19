package kr.co.donghyun.player.data.channel.model

import androidx.room.Entity
import androidx.room.PrimaryKey

data class SearchChannelResponse(
    val message : String,
    val artist : List<ArtistPreview?>
)

@Entity
data class ArtistPreview(
    @PrimaryKey
    val artistId : String,
    val name : String,
    val thumbnailUrl : String,
    val subscribers : String
)