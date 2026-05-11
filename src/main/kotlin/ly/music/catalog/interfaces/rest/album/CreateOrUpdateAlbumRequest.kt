package ly.music.catalog.interfaces.rest.album

import ly.music.catalog.interfaces.rest.shared.LinkRequest
import ly.music.catalog.interfaces.rest.shared.RequestLink

class CreateOrUpdateAlbumRequest(
    val title: String,
    val releasedAt: String? = null,
    val imageUrl: String? = null,
) : LinkRequest() {
    val artists: List<RequestLink>
        get() = requiredLinkList("artists")
}
