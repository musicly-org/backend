package ly.music.catalog.application

import ly.music.catalog.configuration.cache.RELEASES_BY_ALBUM
import ly.music.catalog.configuration.cache.RELEASE_BY_ID
import ly.music.catalog.configuration.cache.RELEASE_DEFAULT_BY_ALBUM
import ly.music.catalog.domain.album.AlbumRepository
import ly.music.catalog.domain.release.ReleaseEntity
import ly.music.catalog.domain.release.ReleaseRepository
import ly.music.catalog.domain.releasesocial.ReleaseSocialEntity
import ly.music.catalog.domain.releasesocial.ReleaseSocialRepository
import ly.music.catalog.domain.track.TrackRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ReleaseService(
    private val releaseRepository: ReleaseRepository,
    private val releaseSocialRepository: ReleaseSocialRepository,
    private val trackRepository: TrackRepository,
    private val albumRepository: AlbumRepository,
) {
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = [RELEASES_BY_ALBUM], sync = true)
    fun findByAlbum(
        albumId: UUID,
        pageable: Pageable,
    ): Page<ReleaseEntity> {
        requireAlbumExists(albumId)
        return releaseRepository.findByAlbumId(albumId, pageable)
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = [RELEASE_BY_ID], sync = true)
    fun findById(id: UUID): ReleaseEntity = releaseRepository.findByIdOrThrow(id)

    @Transactional
    @CacheEvict(cacheNames = [RELEASES_BY_ALBUM, RELEASE_DEFAULT_BY_ALBUM, RELEASE_BY_ID], allEntries = true)
    fun create(command: CreateReleaseCommand): ReleaseEntity {
        val album = albumRepository.findByIdOrThrow(command.albumId)
        val normalizedTitle = ReleaseEntity.normalizeTitle(command.title)

        command.spotifyId?.let { spotifyId ->
            if (releaseRepository.existsBySocialSpotifyId(spotifyId)) {
                throw IllegalArgumentException("Spotify release already linked: $spotifyId")
            }
        }

        val release =
            releaseRepository.save(
                ReleaseEntity(
                    album = album,
                    title = normalizedTitle,
                    releasedAt = command.releasedAt,
                    imageUrl = command.imageUrl,
                    isDefault = !releaseRepository.existsByAlbumIdAndIsDefaultTrue(command.albumId),
                ),
            )
        syncSocial(release, command.spotifyId)
        return release
    }

    @Transactional
    @CacheEvict(cacheNames = [RELEASES_BY_ALBUM, RELEASE_DEFAULT_BY_ALBUM, RELEASE_BY_ID], allEntries = true)
    fun update(command: UpdateReleaseCommand): ReleaseEntity {
        val release = releaseRepository.findByIdOrThrow(command.id)
        val oldAlbum = release.album
        val oldAlbumId = oldAlbum.id
        val targetAlbum = albumRepository.findByIdOrThrow(command.albumId)
        val isMovingToAnotherAlbum = oldAlbumId != targetAlbum.id
        val normalizedTitle = ReleaseEntity.normalizeTitle(command.title)

        command.spotifyId?.let { spotifyId ->
            releaseRepository.findBySocialSpotifyId(spotifyId)?.let { existing ->
                require(existing.id == release.id) { "Spotify release already linked: $spotifyId" }
            }
        }

        release.updateDetails(normalizedTitle, command.releasedAt, command.imageUrl)
        if (!isMovingToAnotherAlbum) {
            syncDefault(release.album.id, release, command.isDefault == true)
            syncSocial(release, command.spotifyId)
            return release
        }

        val oldAlbumReleaseCount = releaseRepository.countByAlbumId(oldAlbumId)
        val targetAlbumHasDefault = releaseRepository.existsByAlbumIdAndIsDefaultTrue(targetAlbum.id)
        val wasDefault = release.isDefault

        release.clearDefault()
        release.moveToAlbum(targetAlbum)
        releaseRepository.saveAndFlush(release)

        if (oldAlbumReleaseCount == 1) {
            albumRepository.delete(oldAlbum)
        } else if (wasDefault) {
            updateDefault(oldAlbumId)
        }

        syncDefault(
            targetAlbum.id,
            release,
            command.isDefault == true || !targetAlbumHasDefault,
        )
        syncSocial(release, command.spotifyId)
        return release
    }

    @Transactional
    @CacheEvict(cacheNames = [RELEASES_BY_ALBUM, RELEASE_DEFAULT_BY_ALBUM, RELEASE_BY_ID], allEntries = true)
    fun patch(command: PatchReleaseCommand): ReleaseEntity {
        val release = releaseRepository.findByIdOrThrow(command.id)
        val oldAlbum = release.album
        val oldAlbumId = oldAlbum.id
        val targetAlbum =
            if (command.hasAlbumId) {
                albumRepository.findByIdOrThrow(command.albumId ?: throw IllegalArgumentException("Missing _links.album"))
            } else {
                oldAlbum
            }
        val isMovingToAnotherAlbum = oldAlbumId != targetAlbum.id
        val normalizedTitle =
            if (command.hasTitle) {
                ReleaseEntity.normalizeTitle(command.title ?: throw IllegalArgumentException("Release title is required"))
            } else {
                release.title
            }
        val releasedAt = if (command.hasReleasedAt) command.releasedAt else release.releasedAt
        val imageUrl = if (command.hasImageUrl) command.imageUrl else release.imageUrl

        if (command.hasSpotifyId) {
            command.spotifyId
                ?.takeIf { it.isNotBlank() }
                ?.let { spotifyId ->
                    releaseRepository.findBySocialSpotifyId(spotifyId)?.let { existing ->
                        require(existing.id == release.id) { "Spotify release already linked: $spotifyId" }
                    }
                }
        }

        release.updateDetails(normalizedTitle, releasedAt, imageUrl)
        if (!isMovingToAnotherAlbum) {
            syncDefault(release.album.id, release, command.hasIsDefault && command.isDefault == true)
            patchSocial(release, command.spotifyId, command.hasSpotifyId)
            return release
        }

        val oldAlbumReleaseCount = releaseRepository.countByAlbumId(oldAlbumId)
        val targetAlbumHasDefault = releaseRepository.existsByAlbumIdAndIsDefaultTrue(targetAlbum.id)
        val wasDefault = release.isDefault

        release.clearDefault()
        release.moveToAlbum(targetAlbum)
        releaseRepository.saveAndFlush(release)

        if (oldAlbumReleaseCount == 1) {
            albumRepository.delete(oldAlbum)
        } else if (wasDefault) {
            updateDefault(oldAlbumId)
        }

        syncDefault(
            targetAlbum.id,
            release,
            (command.hasIsDefault && command.isDefault == true) || !targetAlbumHasDefault,
        )
        patchSocial(release, command.spotifyId, command.hasSpotifyId)
        return release
    }

    @Transactional
    @CacheEvict(cacheNames = [RELEASES_BY_ALBUM, RELEASE_DEFAULT_BY_ALBUM, RELEASE_BY_ID], allEntries = true)
    fun delete(id: UUID) {
        val release = releaseRepository.findByIdOrThrow(id)
        if (releaseRepository.countByAlbumId(release.album.id) == 1) {
            throw IllegalArgumentException("Cannot delete an only release for album")
        }
        if (trackRepository.existsByReleaseId(release.id)) {
            throw IllegalArgumentException("Cannot delete a release that still has tracks")
        }
        releaseSocialRepository.deleteByReleaseId(release.id)
        releaseRepository.delete(release)
        if (release.isDefault) {
            updateDefault(release.album.id)
        }
    }

    private fun requireAlbumExists(id: UUID) {
        if (!albumRepository.existsById(id)) {
            throw NotFoundException("Album", id)
        }
    }

    private fun syncDefault(
        albumId: UUID,
        release: ReleaseEntity,
        shouldBeDefault: Boolean,
    ) {
        if (!shouldBeDefault) {
            return
        }

        releaseRepository
            .findByAlbumIdAndIsDefaultTrue(albumId)
            ?.takeIf { it.id != release.id }
            ?.let { currentDefault ->
                currentDefault.clearDefault()
                releaseRepository.saveAndFlush(currentDefault)
            }
        release.markAsDefault()
    }

    private fun syncSocial(
        release: ReleaseEntity,
        spotifyId: String?,
    ) {
        if (spotifyId.isNullOrBlank()) {
            return
        }

        val existing = releaseSocialRepository.findBySpotifyId(spotifyId)
        require(existing == null || existing.release.id == release.id) { "Spotify release already linked: $spotifyId" }

        val social = release.social
        if (social == null) {
            releaseSocialRepository.save(ReleaseSocialEntity(release = release, spotifyId = spotifyId))
        } else {
            social.spotifyId = spotifyId
        }
    }

    private fun patchSocial(
        release: ReleaseEntity,
        spotifyId: String?,
        provided: Boolean,
    ) {
        if (!provided) {
            return
        }

        if (spotifyId.isNullOrBlank()) {
            release.social = null
            releaseSocialRepository.deleteByReleaseId(release.id)
            return
        }

        syncSocial(release, spotifyId)
    }

    private fun updateDefault(albumId: UUID) {
        val newDefaultRelease =
            releaseRepository.findFirstByAlbumIdOrderByReleasedAtAsc(albumId).apply {
                markAsDefault()
            }
        releaseRepository.save(newDefaultRelease)
    }
}
