package ly.music.catalog.interfaces.rest.song

import ly.music.catalog.interfaces.rest.shared.LinkRequest
import ly.music.catalog.interfaces.rest.shared.RequestLink

class CreateOrUpdateSongRequest(
    val title: String,
    val releasedAt: String? = null,
) : LinkRequest() {
    val artists: List<RequestLink>
        get() = requiredLinkList("artists")
}
