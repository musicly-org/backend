package ly.music.catalog.domain.release

import ly.music.catalog.application.NotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ReleaseRepository : JpaRepository<ReleaseEntity, UUID> {
    fun findBySocialSpotifyId(spotifyId: String): ReleaseEntity?

    fun existsBySocialSpotifyId(spotifyId: String): Boolean

    fun findByAlbumIdAndIsDefaultTrue(albumId: UUID): ReleaseEntity?

    fun existsByAlbumIdAndIsDefaultTrue(albumId: UUID): Boolean

    fun findByAlbumId(
        albumId: UUID,
        pageable: Pageable,
    ): Page<ReleaseEntity>

    fun findByIdOrThrow(id: UUID): ReleaseEntity = findById(id).orElseThrow { NotFoundException("Release", id) }

    fun findFirstByAlbumIdOrderByReleasedAtAsc(id: UUID): ReleaseEntity

    fun countByAlbumId(id: UUID): Int
}
