package ly.music.catalog.domain.song

import ly.music.catalog.application.NotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SongRepository : JpaRepository<SongEntity, UUID> {
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
    ): Page<SongEntity>

    fun findByIdOrThrow(id: UUID): SongEntity = findById(id).orElseThrow { NotFoundException("Song", id) }
}
