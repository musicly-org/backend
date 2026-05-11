package ly.music.catalog.domain.artist

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ArtistRepository : JpaRepository<ArtistEntity, UUID> {
    fun existsByNameIgnoreCase(name: String): Boolean

    fun findDistinctByAlbumsId(albumId: UUID, pageable: Pageable): Page<ArtistEntity>

    fun findDistinctBySongsId(songId: UUID, pageable: Pageable): Page<ArtistEntity>
}
