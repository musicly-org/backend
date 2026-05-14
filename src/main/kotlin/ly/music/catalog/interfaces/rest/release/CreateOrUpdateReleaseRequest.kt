package ly.music.catalog.interfaces.rest.release

import ly.music.catalog.interfaces.rest.shared.LinkRequest
import ly.music.catalog.interfaces.rest.shared.RequestLink

class CreateOrUpdateReleaseRequest(
    val title: String,
    val releasedAt: String? = null,
    val imageUrl: String? = null,
    val spotifyId: String? = null,
    val isDefault: Boolean? = null,
) : LinkRequest() {
    val album: RequestLink
        get() = requiredLink("album")
}
