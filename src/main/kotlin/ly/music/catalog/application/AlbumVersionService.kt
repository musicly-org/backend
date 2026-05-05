package ly.music.catalog.application

import ly.music.catalog.domain.album.AlbumRepository
import ly.music.catalog.domain.albumversion.AlbumVersionEntity
import ly.music.catalog.domain.albumversion.AlbumVersionRepository
import ly.music.catalog.domain.albumversiontrack.AlbumVersionTrackEntity
import ly.music.catalog.domain.albumversiontrack.AlbumVersionTrackRepository
import ly.music.catalog.configuration.cache.ALBUM_VERSION_BY_ID
import ly.music.catalog.configuration.cache.ALBUM_VERSION_DEFAULT_BY_ALBUM
import ly.music.catalog.configuration.cache.ALBUM_VERSION_TRACKS_BY_VERSION
import ly.music.catalog.configuration.cache.ALBUM_VERSIONS_BY_ALBUM
import ly.music.catalog.configuration.cache.ALBUM_VERSION_TRACK_BY_ID
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class AlbumVersionService(
    private val albumVersionRepository: AlbumVersionRepository,
    private val albumRepository: AlbumRepository,
    private val albumVersionTrackRepository: AlbumVersionTrackRepository,
) {
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = [ALBUM_VERSIONS_BY_ALBUM], sync = true)
    fun findByAlbum(
        albumId: UUID,
        pageable: Pageable,
    ): Page<AlbumVersionEntity> {
        requireAlbumExists(albumId)
        return albumVersionRepository.findByAlbumId(albumId, pageable)
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = [ALBUM_VERSION_DEFAULT_BY_ALBUM], sync = true)
    fun findDefaultByAlbum(albumId: UUID): AlbumVersionEntity {
        requireAlbumExists(albumId)
        return albumVersionRepository.findByAlbumIdAndIsDefaultTrue(albumId)
            ?: albumVersionRepository.findByAlbumId(albumId, Pageable.unpaged()).content.firstOrNull()
            ?: throw NotFoundException("AlbumVersion", albumId)
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = [ALBUM_VERSION_TRACKS_BY_VERSION], sync = true)
    fun findTracks(
        albumVersionId: UUID,
        pageable: Pageable,
    ): Page<AlbumVersionTrackEntity> {
        findById(albumVersionId)
        return albumVersionTrackRepository.findByAlbumVersionId(albumVersionId, pageable)
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = [ALBUM_VERSION_BY_ID], sync = true)
    fun findById(id: UUID): AlbumVersionEntity = albumVersionRepository.findById(id).orElseThrow { NotFoundException("AlbumVersion", id) }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = [ALBUM_VERSION_TRACK_BY_ID], sync = true)
    fun findTrackById(id: UUID): AlbumVersionTrackEntity =
        albumVersionTrackRepository.findById(id).orElseThrow { NotFoundException("AlbumVersionTrack", id) }

    @Transactional
    @CacheEvict(
        cacheNames = [
            ALBUM_VERSIONS_BY_ALBUM,
            ALBUM_VERSION_DEFAULT_BY_ALBUM,
            ALBUM_VERSION_BY_ID,
            ALBUM_VERSION_TRACKS_BY_VERSION,
            ALBUM_VERSION_TRACK_BY_ID,
        ],
        allEntries = true,
    )
    fun create(command: CreateAlbumVersionCommand): AlbumVersionEntity {
        val album =
            albumRepository.findById(command.albumId).orElseThrow {
                NotFoundException("Album", command.albumId)
            }
        val normalizedTitle = AlbumVersionEntity.normalizeTitle(command.title)

        require(!albumVersionRepository.existsByAlbumIdAndTitleIgnoreCase(command.albumId, normalizedTitle)) {
            "Album version already exists for album: $normalizedTitle"
        }

        return albumVersionRepository.save(
            AlbumVersionEntity(
                album = album,
                title = normalizedTitle,
                releasedAt = command.releasedAt,
                isDefault = !albumVersionRepository.existsByAlbumIdAndIsDefaultTrue(command.albumId),
            ),
        )
    }

    @Transactional
    @CacheEvict(
        cacheNames = [
            ALBUM_VERSIONS_BY_ALBUM,
            ALBUM_VERSION_DEFAULT_BY_ALBUM,
            ALBUM_VERSION_BY_ID,
            ALBUM_VERSION_TRACKS_BY_VERSION,
            ALBUM_VERSION_TRACK_BY_ID,
        ],
        allEntries = true,
    )
    fun update(command: UpdateAlbumVersionCommand): AlbumVersionEntity {
        val albumVersion = findById(command.id)
        val normalizedTitle = AlbumVersionEntity.normalizeTitle(command.title)

        if (!albumVersion.hasTitle(normalizedTitle)) {
            require(!albumVersionRepository.existsByAlbumIdAndTitleIgnoreCase(albumVersion.album.id, normalizedTitle)) {
                "Album version already exists for album: $normalizedTitle"
            }
        }

        albumVersion.updateDetails(normalizedTitle, command.releasedAt)
        return albumVersion
    }

    @Transactional
    @CacheEvict(
        cacheNames = [
            ALBUM_VERSIONS_BY_ALBUM,
            ALBUM_VERSION_DEFAULT_BY_ALBUM,
            ALBUM_VERSION_BY_ID,
            ALBUM_VERSION_TRACKS_BY_VERSION,
            ALBUM_VERSION_TRACK_BY_ID,
        ],
        allEntries = true,
    )
    fun delete(id: UUID) {
        val albumVersion = findById(id)
        if (albumVersion.isDefault) {
            val replacement =
                albumVersionRepository.findFirstByAlbumIdAndIsDefaultFalseOrderByCreatedAtAsc(albumVersion.album.id)
                    ?: throw IllegalStateException("Album must keep a default version")
            replacement.markAsDefault()
        }
        albumVersionRepository.delete(albumVersion)
    }

    private fun requireAlbumExists(id: UUID) {
        if (!albumRepository.existsById(id)) {
            throw NotFoundException("Album", id)
        }
    }
}
