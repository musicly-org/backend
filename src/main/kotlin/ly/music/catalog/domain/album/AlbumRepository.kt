package ly.music.catalog.domain.album

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AlbumRepository : JpaRepository<AlbumEntity, UUID> {
    fun findDistinctByArtistsId(
        artistId: UUID,
        pageable: Pageable,
    ): Page<AlbumEntity>
}
