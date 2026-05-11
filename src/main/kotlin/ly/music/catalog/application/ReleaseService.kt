package ly.music.catalog.application

import ly.music.catalog.configuration.cache.RELEASES_BY_ALBUM
import ly.music.catalog.configuration.cache.RELEASE_BY_ID
import ly.music.catalog.configuration.cache.RELEASE_DEFAULT_BY_ALBUM
import ly.music.catalog.domain.album.AlbumRepository
import ly.music.catalog.domain.release.ReleaseEntity
import ly.music.catalog.domain.release.ReleaseRepository
import ly.music.catalog.domain.releasesocial.ReleaseSocialEntity
import ly.music.catalog.domain.releasesocial.ReleaseSocialRepository
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
    @Cacheable(cacheNames = [RELEASE_DEFAULT_BY_ALBUM], sync = true)
    fun findDefaultByAlbum(albumId: UUID): ReleaseEntity {
        requireAlbumExists(albumId)
        return releaseRepository.findByAlbumIdAndIsDefaultTrue(albumId)
            ?: releaseRepository.findByAlbumId(albumId, Pageable.unpaged()).content.firstOrNull()
            ?: throw NotFoundException("Release", albumId)
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = [RELEASE_BY_ID], sync = true)
    fun findById(id: UUID): ReleaseEntity = releaseRepository.findById(id).orElseThrow { NotFoundException("Release", id) }

    @Transactional
    @CacheEvict(cacheNames = [RELEASES_BY_ALBUM, RELEASE_DEFAULT_BY_ALBUM, RELEASE_BY_ID], allEntries = true)
    fun create(command: CreateReleaseCommand): ReleaseEntity {
        val album = albumRepository.findById(command.albumId).orElseThrow { NotFoundException("Album", command.albumId) }
        val normalizedTitle = ReleaseEntity.normalizeTitle(command.title)

        command.spotifyId?.let { spotifyId ->
            releaseRepository.findBySocialSpotifyId(spotifyId)?.let { existing ->
                existing.updateDetails(normalizedTitle, command.releasedAt, command.imageUrl ?: existing.imageUrl)
                syncDefault(existing.album.id, existing, command.isDefault == true)
                syncSocial(existing, spotifyId)
                return existing
            }
        }

        require(!releaseRepository.existsByAlbumIdAndTitleIgnoreCase(command.albumId, normalizedTitle)) {
            "Release already exists for album: $normalizedTitle"
        }

        val release = releaseRepository.save(
            ReleaseEntity(
                album = album,
                title = normalizedTitle,
                releasedAt = command.releasedAt,
                imageUrl = command.imageUrl,
                isDefault = command.isDefault ?: !releaseRepository.existsByAlbumIdAndIsDefaultTrue(command.albumId),
            ),
        )
        syncDefault(album.id, release, command.isDefault == true)
        syncSocial(release, command.spotifyId)
        return release
    }

    @Transactional
    @CacheEvict(cacheNames = [RELEASES_BY_ALBUM, RELEASE_DEFAULT_BY_ALBUM, RELEASE_BY_ID], allEntries = true)
    fun update(command: UpdateReleaseCommand): ReleaseEntity {
        val release = findById(command.id)
        val normalizedTitle = ReleaseEntity.normalizeTitle(command.title)

        if (!release.hasTitle(normalizedTitle)) {
            require(!releaseRepository.existsByAlbumIdAndTitleIgnoreCase(release.album.id, normalizedTitle)) {
                "Release already exists for album: $normalizedTitle"
            }
        }

        command.spotifyId?.let { spotifyId ->
            releaseRepository.findBySocialSpotifyId(spotifyId)?.let { existing ->
                require(existing.id == release.id) { "Spotify release already linked: $spotifyId" }
            }
        }

        release.updateDetails(normalizedTitle, command.releasedAt, command.imageUrl)
        syncDefault(release.album.id, release, command.isDefault == true)
        syncSocial(release, command.spotifyId)
        return release
    }

    @Transactional
    @CacheEvict(cacheNames = [RELEASES_BY_ALBUM, RELEASE_DEFAULT_BY_ALBUM, RELEASE_BY_ID], allEntries = true)
    fun delete(id: UUID) {
        val release = findById(id)
        if (release.isDefault) {
            val replacement =
                releaseRepository.findFirstByAlbumIdAndIsDefaultFalseOrderByCreatedAtAsc(release.album.id)
                    ?: throw IllegalStateException("Album must keep a default release")
            replacement.markAsDefault()
        }
        releaseRepository.delete(release)
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

        releaseRepository.findByAlbumIdAndIsDefaultTrue(albumId)
            ?.takeIf { it.id != release.id }
            ?.clearDefault()
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
}
