package ly.music.catalog.domain.release

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ReleaseRepository : JpaRepository<ReleaseEntity, UUID> {
    fun existsByAlbumIdAndTitleIgnoreCase(
        albumId: UUID,
        title: String,
    ): Boolean

    fun findByAlbumIdAndIsDefaultTrue(albumId: UUID): ReleaseEntity?

    fun existsByAlbumIdAndIsDefaultTrue(albumId: UUID): Boolean

    fun findByAlbumId(
        albumId: UUID,
        pageable: Pageable,
    ): Page<ReleaseEntity>

    fun findByAlbumIdAndIsDefaultFalse(
        albumId: UUID,
        pageable: Pageable,
    ): Page<ReleaseEntity>

    fun findFirstByAlbumIdAndIsDefaultFalseOrderByCreatedAtAsc(albumId: UUID): ReleaseEntity?
}
