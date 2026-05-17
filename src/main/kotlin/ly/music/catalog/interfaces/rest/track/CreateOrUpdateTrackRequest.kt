package ly.music.catalog.interfaces.rest.track

import ly.music.catalog.interfaces.rest.shared.LinkRequest
import ly.music.catalog.interfaces.rest.shared.RequestLink

class CreateOrUpdateTrackRequest(
    val title: String,
    val imageUrl: String? = null,
    val durationSeconds: Int? = null,
    val releasedAt: String? = null,
    val discNumber: Int,
    val trackNumber: Int,
    val spotifyId: String? = null,
) : LinkRequest() {
    val song: RequestLink
        get() = requiredLink("song")

    val release: RequestLink
        get() = requiredLink("release")
}
