package ly.music.catalog.domain.albumversion

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AlbumVersionRepository : JpaRepository<AlbumVersionEntity, UUID> {
    fun existsByAlbumIdAndTitleIgnoreCase(
        albumId: UUID,
        title: String,
    ): Boolean

    fun findByAlbumIdAndIsDefaultTrue(albumId: UUID): AlbumVersionEntity?

    fun existsByAlbumIdAndIsDefaultTrue(albumId: UUID): Boolean

    fun findByAlbumId(
        albumId: UUID,
        pageable: Pageable,
    ): Page<AlbumVersionEntity>

    fun findByAlbumIdAndIsDefaultFalse(
        albumId: UUID,
        pageable: Pageable,
    ): Page<AlbumVersionEntity>

    fun findFirstByAlbumIdAndIsDefaultFalseOrderByCreatedAtAsc(albumId: UUID): AlbumVersionEntity?
}
