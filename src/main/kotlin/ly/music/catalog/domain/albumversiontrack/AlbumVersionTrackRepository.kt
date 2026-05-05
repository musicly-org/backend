package ly.music.catalog.domain.albumversiontrack

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AlbumVersionTrackRepository : JpaRepository<AlbumVersionTrackEntity, UUID> {
    fun existsByAlbumVersionIdAndDiscNumberAndTrackNumber(
        albumVersionId: UUID,
        discNumber: Int,
        trackNumber: Int,
    ): Boolean

    fun findByAlbumVersionId(
        albumVersionId: UUID,
        pageable: Pageable,
    ): Page<AlbumVersionTrackEntity>
}
