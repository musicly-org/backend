package ly.music.catalog.application

import ly.music.catalog.domain.release.ReleasedAt
import ly.music.catalog.interfaces.rest.release.CreateReleaseRequest
import ly.music.catalog.interfaces.rest.release.PatchReleaseRequest
import ly.music.catalog.interfaces.rest.release.UpdateReleaseRequest
import java.util.UUID

data class CreateArtistCommand(
    val name: String,
    val imageUrl: String? = null,
    val spotifyId: String? = null,
)

data class UpdateArtistCommand(
    val id: UUID,
    val name: String,
    val imageUrl: String? = null,
    val spotifyId: String? = null,
)

data class CreateAlbumCommand(
    val artistIds: Set<UUID>,
    val title: String,
    val releasedAt: ReleasedAt? = null,
    val imageUrl: String? = null,
)

data class UpdateAlbumCommand(
    val id: UUID,
    val artistIds: Set<UUID>,
    val title: String,
    val releasedAt: ReleasedAt? = null,
    val imageUrl: String? = null,
)

data class CreateReleaseCommand(
    val albumId: UUID,
    val title: String,
    val releasedAt: ReleasedAt? = null,
    val imageUrl: String? = null,
    val spotifyId: String? = null,
) {
    constructor(request: CreateReleaseRequest) : this(
        albumId = request.album.uuid(),
        title = request.title,
        releasedAt = ReleasedAt.parseOrNull(request.releasedAt),
        imageUrl = request.imageUrl,
        spotifyId = request.spotifyId,
    )
}

data class UpdateReleaseCommand(
    val id: UUID,
    val albumId: UUID,
    val title: String,
    val releasedAt: ReleasedAt? = null,
    val imageUrl: String? = null,
    val spotifyId: String? = null,
    val isDefault: Boolean? = null,
) {
    constructor(id: UUID, request: UpdateReleaseRequest) : this(
        id = id,
        albumId = request.album.uuid(),
        title = request.title,
        releasedAt = ReleasedAt.parseOrNull(request.releasedAt),
        imageUrl = request.imageUrl,
        spotifyId = request.spotifyId,
        isDefault = request.isDefault,
    )
}

data class PatchReleaseCommand(
    val id: UUID,
    val title: String? = null,
    val hasTitle: Boolean = false,
    val releasedAt: ReleasedAt? = null,
    val hasReleasedAt: Boolean = false,
    val imageUrl: String? = null,
    val hasImageUrl: Boolean = false,
    val spotifyId: String? = null,
    val hasSpotifyId: Boolean = false,
    val isDefault: Boolean? = null,
    val hasIsDefault: Boolean = false,
    val albumId: UUID? = null,
    val hasAlbumId: Boolean = false,
) {
    constructor(id: UUID, request: PatchReleaseRequest) : this(
        id = id,
        title = request.title,
        hasTitle = request.hasTitle,
        releasedAt = if (request.hasReleasedAt) ReleasedAt.parseOrNull(request.releasedAt) else null,
        hasReleasedAt = request.hasReleasedAt,
        imageUrl = request.imageUrl,
        hasImageUrl = request.hasImageUrl,
        spotifyId = request.spotifyId,
        hasSpotifyId = request.hasSpotifyId,
        isDefault = request.isDefault,
        hasIsDefault = request.hasIsDefault,
        albumId = request.album?.uuid(),
        hasAlbumId = request.hasAlbum,
    )
}

data class CreateSongCommand(
    val artistIds: Set<UUID>,
    val title: String,
    val releasedAt: ReleasedAt? = null,
)

data class UpdateSongCommand(
    val id: UUID,
    val artistIds: Set<UUID>,
    val title: String,
    val releasedAt: ReleasedAt? = null,
)

data class CreateTrackCommand(
    val songId: UUID,
    val releaseId: UUID,
    val title: String,
    val imageUrl: String? = null,
    val durationSeconds: Int? = null,
    val releasedAt: ReleasedAt? = null,
    val discNumber: Int,
    val trackNumber: Int,
    val spotifyId: String? = null,
)

data class UpdateTrackCommand(
    val id: UUID,
    val songId: UUID,
    val releaseId: UUID,
    val title: String,
    val imageUrl: String? = null,
    val durationSeconds: Int? = null,
    val releasedAt: ReleasedAt? = null,
    val discNumber: Int,
    val trackNumber: Int,
    val spotifyId: String? = null,
)
