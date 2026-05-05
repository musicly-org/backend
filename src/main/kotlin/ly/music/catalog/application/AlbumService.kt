package ly.music.catalog.application

import ly.music.catalog.domain.album.AlbumEntity
import ly.music.catalog.domain.album.AlbumRepository
import ly.music.catalog.domain.albumversion.AlbumVersionEntity
import ly.music.catalog.domain.artist.ArtistRepository
import ly.music.catalog.configuration.cache.ALBUMS_BY_ARTIST
import ly.music.catalog.configuration.cache.ALBUM_BY_ID
import ly.music.catalog.configuration.cache.ALBUM_VERSION_BY_ID
import ly.music.catalog.configuration.cache.ALBUM_VERSION_DEFAULT_BY_ALBUM
import ly.music.catalog.configuration.cache.ALBUM_VERSION_TRACKS_BY_VERSION
import ly.music.catalog.configuration.cache.ALBUM_VERSION_TRACK_BY_ID
import ly.music.catalog.configuration.cache.ALBUM_VERSIONS_BY_ALBUM
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class AlbumService(
    private val albumRepository: AlbumRepository,
    private val artistRepository: ArtistRepository,
) {
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = [ALBUMS_BY_ARTIST], sync = true)
    fun findByArtist(
        artistId: UUID,
        pageable: Pageable,
    ): Page<AlbumEntity> {
        requireArtistExists(artistId)
        return albumRepository.findByArtistId(artistId, pageable)
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = [ALBUM_BY_ID], sync = true)
    fun findById(id: UUID): AlbumEntity =
        albumRepository.findById(id).orElseThrow { NotFoundException("Album", id) }

    @Transactional
    @CacheEvict(cacheNames = [ALBUMS_BY_ARTIST], allEntries = true)
    fun create(command: CreateAlbumCommand): AlbumEntity {
        val artist = artistRepository.findById(command.artistId).orElseThrow {
            NotFoundException("Artist", command.artistId)
        }
        val normalizedTitle = AlbumEntity.normalizeTitle(command.title)

        require(!albumRepository.existsByArtistIdAndTitleIgnoreCase(command.artistId, normalizedTitle)) {
            "Album already exists for artist: $normalizedTitle"
        }

        val album =
            AlbumEntity(
                artist = artist,
                title = normalizedTitle,
                releasedAt = command.releasedAt,
            )
        album.versions.add(
            AlbumVersionEntity(
                album = album,
                title = normalizedTitle,
                releasedAt = command.releasedAt,
                isDefault = true,
            ),
        )

        return albumRepository.save(album)
    }

    @Transactional
    @CacheEvict(cacheNames = [ALBUMS_BY_ARTIST, ALBUM_BY_ID], allEntries = true)
    fun rename(command: RenameAlbumCommand): AlbumEntity {
        val album = findById(command.id)
        val normalizedTitle = AlbumEntity.normalizeTitle(command.title)

        if (!album.hasTitle(normalizedTitle)) {
            require(!albumRepository.existsByArtistIdAndTitleIgnoreCase(album.artist.id, normalizedTitle)) {
                "Album already exists for artist: $normalizedTitle"
            }
        }

        album.updateDetails(normalizedTitle, command.releasedAt)
        return album
    }

    @Transactional
    @CacheEvict(
        cacheNames = [
            ALBUMS_BY_ARTIST,
            ALBUM_BY_ID,
            ALBUM_VERSIONS_BY_ALBUM,
            ALBUM_VERSION_BY_ID,
            ALBUM_VERSION_DEFAULT_BY_ALBUM,
            ALBUM_VERSION_TRACKS_BY_VERSION,
            ALBUM_VERSION_TRACK_BY_ID,
        ],
        allEntries = true,
    )
    fun delete(id: UUID) {
        val album = findById(id)
        albumRepository.delete(album)
    }

    private fun requireArtistExists(id: UUID) {
        if (!artistRepository.existsById(id)) {
            throw NotFoundException("Artist", id)
        }
    }
}
