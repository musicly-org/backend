package ly.music.catalog.domain.songversion

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SongVersionRepository : JpaRepository<SongVersionEntity, UUID> {
    fun existsBySongIdAndTitleIgnoreCase(songId: UUID, title: String): Boolean

    fun findBySongId(
        songId: UUID,
        pageable: Pageable,
    ): Page<SongVersionEntity>
}
