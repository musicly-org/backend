package ly.music.catalog.domain.artist

import ly.music.catalog.application.NotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ArtistRepository : JpaRepository<ArtistEntity, UUID> {
    fun findBySocialSpotifyId(spotifyId: String): ArtistEntity?

    fun findDistinctByAlbumsId(
        albumId: UUID,
        pageable: Pageable,
    ): Page<ArtistEntity>

    fun findDistinctBySongsId(
        songId: UUID,
        pageable: Pageable,
    ): Page<ArtistEntity>

    fun findByIdOrThrow(id: UUID): ArtistEntity = findById(id).orElseThrow { NotFoundException("Artist", id) }
}
