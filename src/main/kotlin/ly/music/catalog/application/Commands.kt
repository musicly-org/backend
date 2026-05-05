package ly.music.catalog.application

import ly.music.catalog.domain.release.ReleasedAt
import java.util.UUID

data class CreateArtistCommand(
    val name: String,
)

data class RenameArtistCommand(
    val id: UUID,
    val name: String,
)

data class CreateAlbumCommand(
    val artistId: UUID,
    val title: String,
    val releasedAt: ReleasedAt? = null,
)

data class RenameAlbumCommand(
    val id: UUID,
    val title: String,
    val releasedAt: ReleasedAt? = null,
)

data class CreateAlbumVersionCommand(
    val albumId: UUID,
    val title: String,
    val releasedAt: ReleasedAt? = null,
)

data class UpdateAlbumVersionCommand(
    val id: UUID,
    val title: String,
    val releasedAt: ReleasedAt? = null,
)

data class CreateSongCommand(
    val artistId: UUID,
    val title: String,
    val releasedAt: ReleasedAt? = null,
)

data class RenameSongCommand(
    val id: UUID,
    val title: String,
    val releasedAt: ReleasedAt? = null,
)
