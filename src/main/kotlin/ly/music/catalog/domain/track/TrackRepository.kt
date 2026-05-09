package ly.music.catalog.domain.track

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TrackRepository : JpaRepository<TrackEntity, UUID> {
    fun existsByReleaseIdAndDiscNumberAndTrackNumber(
        releaseId: UUID,
        discNumber: Int,
        trackNumber: Int,
    ): Boolean

    fun findByReleaseId(
        releaseId: UUID,
        pageable: Pageable,
    ): Page<TrackEntity>

    fun findBySongId(
        songId: UUID,
        pageable: Pageable,
    ): Page<TrackEntity>
}
