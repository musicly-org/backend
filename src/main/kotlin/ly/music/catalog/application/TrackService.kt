package ly.music.catalog.application

import ly.music.catalog.configuration.cache.TRACKS_BY_RELEASE
import ly.music.catalog.configuration.cache.TRACKS_BY_SONG
import ly.music.catalog.configuration.cache.TRACK_BY_ID
import ly.music.catalog.domain.release.ReleaseRepository
import ly.music.catalog.domain.song.SongRepository
import ly.music.catalog.domain.track.TrackEntity
import ly.music.catalog.domain.track.TrackRepository
import ly.music.catalog.domain.tracksocial.TrackSocialEntity
import ly.music.catalog.domain.tracksocial.TrackSocialRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class TrackService(
    private val trackRepository: TrackRepository,
    private val trackSocialRepository: TrackSocialRepository,
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

    @Transactional
    @CacheEvict(cacheNames = [TRACKS_BY_RELEASE, TRACKS_BY_SONG, TRACK_BY_ID], allEntries = true)
    fun create(command: CreateTrackCommand): TrackEntity {
        val release = releaseRepository.findById(command.releaseId).orElseThrow { NotFoundException("Release", command.releaseId) }
        val song = songRepository.findById(command.songId).orElseThrow { NotFoundException("Song", command.songId) }

        command.spotifyId?.let { spotifyId ->
            trackRepository.findBySocialSpotifyId(spotifyId)?.let { existing ->
                existing.song = song
                existing.release = release
                existing.updateDetails(
                    command.title,
                    command.imageUrl,
                    command.durationSeconds,
                    command.releasedAt,
                    command.discNumber,
                    command.trackNumber,
                )
                syncSocial(existing, spotifyId)
                return existing
            }
        }

        require(!trackRepository.existsByReleaseIdAndDiscNumberAndTrackNumber(command.releaseId, command.discNumber, command.trackNumber)) {
            "Track position already exists for release"
        }

        val track = trackRepository.save(
            TrackEntity(
                song = song,
                release = release,
                title = command.title,
                imageUrl = command.imageUrl,
                durationSeconds = command.durationSeconds,
                releasedAt = command.releasedAt,
                discNumber = command.discNumber,
                trackNumber = command.trackNumber,
            ),
        )
        syncSocial(track, command.spotifyId)
        return track
    }

    @Transactional
    @CacheEvict(cacheNames = [TRACKS_BY_RELEASE, TRACKS_BY_SONG, TRACK_BY_ID], allEntries = true)
    fun update(command: UpdateTrackCommand): TrackEntity {
        val track = findById(command.id)
        val release = releaseRepository.findById(command.releaseId).orElseThrow { NotFoundException("Release", command.releaseId) }
        val song = songRepository.findById(command.songId).orElseThrow { NotFoundException("Song", command.songId) }

        if (track.release.id != command.releaseId || track.discNumber != command.discNumber || track.trackNumber != command.trackNumber) {
            require(!trackRepository.existsByReleaseIdAndDiscNumberAndTrackNumber(command.releaseId, command.discNumber, command.trackNumber)) {
                "Track position already exists for release"
            }
        }

        command.spotifyId?.let { spotifyId ->
            trackRepository.findBySocialSpotifyId(spotifyId)?.let { existing ->
                require(existing.id == track.id) { "Spotify track already linked: $spotifyId" }
            }
        }

        track.song = song
        track.release = release
        track.updateDetails(
            command.title,
            command.imageUrl,
            command.durationSeconds,
            command.releasedAt,
            command.discNumber,
            command.trackNumber,
        )
        syncSocial(track, command.spotifyId)
        return track
    }

    @Transactional
    @CacheEvict(cacheNames = [TRACKS_BY_RELEASE, TRACKS_BY_SONG, TRACK_BY_ID], allEntries = true)
    fun delete(id: UUID) {
        trackRepository.delete(findById(id))
    }

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

    private fun syncSocial(
        track: TrackEntity,
        spotifyId: String?,
    ) {
        if (spotifyId.isNullOrBlank()) {
            return
        }

        val existing = trackSocialRepository.findBySpotifyId(spotifyId)
        require(existing == null || existing.track.id == track.id) { "Spotify track already linked: $spotifyId" }

        val social = track.social
        if (social == null) {
            trackSocialRepository.save(TrackSocialEntity(track = track, spotifyId = spotifyId))
        } else {
            social.spotifyId = spotifyId
        }
    }
}
