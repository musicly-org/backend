package ly.music.catalog.interfaces.rest.release

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonSetter
import ly.music.catalog.interfaces.rest.shared.LinkRequest
import ly.music.catalog.interfaces.rest.shared.RequestLink

class PatchReleaseRequest : LinkRequest() {
    var title: String? = null
        private set
    var releasedAt: String? = null
        private set
    var imageUrl: String? = null
        private set
    var spotifyId: String? = null
        private set
    var isDefault: Boolean? = null
        private set

    var hasTitle: Boolean = false
        private set
    var hasReleasedAt: Boolean = false
        private set
    var hasImageUrl: Boolean = false
        private set
    var hasSpotifyId: Boolean = false
        private set
    var hasIsDefault: Boolean = false
        private set

    val album: RequestLink?
        get() = optionalLink("album")

    val hasAlbum: Boolean
        get() = hasLink("album")

    @JsonSetter("title")
    fun applyTitle(value: String?) {
        title = value
        hasTitle = true
    }

    @JsonSetter("releasedAt")
    fun applyReleasedAt(value: String?) {
        releasedAt = value
        hasReleasedAt = true
    }

    @JsonSetter("imageUrl")
    fun applyImageUrl(value: String?) {
        imageUrl = value
        hasImageUrl = true
    }

    @JsonSetter("spotifyId")
    fun applySpotifyId(value: String?) {
        spotifyId = value
        hasSpotifyId = true
    }

    @JsonSetter("isDefault")
    fun applyIsDefault(value: Boolean?) {
        isDefault = value
        hasIsDefault = true
    }

    @JsonAnySetter
    fun rejectUnknownField(name: String, value: Any?) {
        throw IllegalArgumentException("Unknown field: $name")
    }
}
