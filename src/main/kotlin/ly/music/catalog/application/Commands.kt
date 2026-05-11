package ly.music.catalog.application

import ly.music.catalog.domain.release.ReleasedAt
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
    val isDefault: Boolean? = null,
)

data class UpdateReleaseCommand(
    val id: UUID,
    val title: String,
    val releasedAt: ReleasedAt? = null,
    val imageUrl: String? = null,
    val spotifyId: String? = null,
    val isDefault: Boolean? = null,
)

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
