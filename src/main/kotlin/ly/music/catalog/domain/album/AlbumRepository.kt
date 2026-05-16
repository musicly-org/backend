package ly.music.catalog.domain.album

import ly.music.catalog.application.NotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AlbumRepository : JpaRepository<AlbumEntity, UUID> {
    fun existsByArtistsId(artistId: UUID): Boolean

    fun existsByArtistsIdAndTitleIgnoreCase(
        artistId: UUID,
        title: String,
    ): Boolean

    fun existsByArtistsIdAndTitleIgnoreCaseAndIdNot(
        artistId: UUID,
        title: String,
        id: UUID,
    ): Boolean

    fun findDistinctByArtistsId(
        artistId: UUID,
        pageable: Pageable,
    ): Page<AlbumEntity>

    fun findByIdOrThrow(id: UUID): AlbumEntity = findById(id).orElseThrow { NotFoundException("Album", id) }
}
