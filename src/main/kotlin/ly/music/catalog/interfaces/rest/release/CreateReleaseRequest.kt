package ly.music.catalog.interfaces.rest.release

import com.fasterxml.jackson.annotation.JsonAnySetter
import ly.music.catalog.interfaces.rest.shared.LinkRequest
import ly.music.catalog.interfaces.rest.shared.RequestLink

class CreateReleaseRequest(
    val title: String,
    val releasedAt: String? = null,
    val imageUrl: String? = null,
    val spotifyId: String? = null,
) : LinkRequest() {
    val album: RequestLink
        get() = requiredLink("album")

    @JsonAnySetter
    fun rejectUnknownField(name: String, value: Any?) {
        throw IllegalArgumentException("Unknown field: $name")
    }
}
