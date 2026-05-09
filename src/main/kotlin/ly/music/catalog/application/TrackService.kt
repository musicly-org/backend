package ly.music.catalog.application

import ly.music.catalog.configuration.cache.TRACKS_BY_RELEASE
import ly.music.catalog.configuration.cache.TRACKS_BY_SONG
import ly.music.catalog.configuration.cache.TRACK_BY_ID
import ly.music.catalog.domain.release.ReleaseRepository
import ly.music.catalog.domain.song.SongRepository
import ly.music.catalog.domain.track.TrackEntity
import ly.music.catalog.domain.track.TrackRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class TrackService(
    private val trackRepository: TrackRepository,
    private val releaseRepository: ReleaseRepository,
    private val songRepository: SongRepository,
) {
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = [TRACKS_BY_RELEASE], sync = true)
    fun findByRelease(
        releaseId: UUID,
        pageable: Pageable,
    ): Page<TrackEntity> {
        requireReleaseExists(releaseId)
        return trackRepository.findByReleaseId(releaseId, pageable)
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = [TRACKS_BY_SONG], sync = true)
    fun findBySong(
        songId: UUID,
        pageable: Pageable,
    ): Page<TrackEntity> {
        requireSongExists(songId)
        return trackRepository.findBySongId(songId, pageable)
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = [TRACK_BY_ID], sync = true)
    fun findById(id: UUID): TrackEntity =
        trackRepository.findById(id).orElseThrow { NotFoundException("Track", id) }

    private fun requireSongExists(id: UUID) {
        if (!songRepository.existsById(id)) {
            throw NotFoundException("Song", id)
        }
    }

    private fun requireReleaseExists(id: UUID) {
        if (!releaseRepository.existsById(id)) {
            throw NotFoundException("Release", id)
        }
    }
}
