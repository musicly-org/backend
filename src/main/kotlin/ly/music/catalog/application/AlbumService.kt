package ly.music.catalog.application

import ly.music.catalog.domain.album.AlbumEntity
import ly.music.catalog.domain.album.AlbumRepository
import ly.music.catalog.domain.artist.ArtistRepository
import ly.music.catalog.domain.release.ReleaseEntity
import ly.music.catalog.configuration.cache.ALBUMS_BY_ARTIST
import ly.music.catalog.configuration.cache.ALBUM_BY_ID
import ly.music.catalog.configuration.cache.RELEASE_BY_ID
import ly.music.catalog.configuration.cache.RELEASE_DEFAULT_BY_ALBUM
import ly.music.catalog.configuration.cache.RELEASES_BY_ALBUM
import ly.music.catalog.configuration.cache.TRACKS_BY_RELEASE
import ly.music.catalog.configuration.cache.TRACK_BY_ID
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
        return albumRepository.findDistinctByArtistsId(artistId, pageable)
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = [ALBUM_BY_ID], sync = true)
    fun findById(id: UUID): AlbumEntity =
        albumRepository.findById(id).orElseThrow { NotFoundException("Album", id) }

    @Transactional
    @CacheEvict(cacheNames = [ALBUMS_BY_ARTIST], allEntries = true)
    fun create(command: CreateAlbumCommand): AlbumEntity {
        val artists = resolveArtists(command.artistIds)
        val normalizedTitle = AlbumEntity.normalizeTitle(command.title)

        val album =
            AlbumEntity(
                title = normalizedTitle,
                releasedAt = command.releasedAt,
            )
        album.artists.addAll(artists)
        album.releases.add(
            ReleaseEntity(
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

        album.updateDetails(normalizedTitle, command.releasedAt)
        return album
    }

    @Transactional
    @CacheEvict(
        cacheNames = [
            ALBUMS_BY_ARTIST,
            ALBUM_BY_ID,
            RELEASES_BY_ALBUM,
            RELEASE_BY_ID,
            RELEASE_DEFAULT_BY_ALBUM,
            TRACKS_BY_RELEASE,
            TRACK_BY_ID,
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

    private fun resolveArtists(ids: Set<UUID>) =
        ids
            .takeIf { it.isNotEmpty() }
            ?.let { artistRepository.findAllById(it).toSet() }
            ?.also { artists ->
                val missingIds = ids - artists.map { it.id }.toSet()
                require(missingIds.isEmpty()) { "Artists not found: ${missingIds.joinToString(",")}" }
            }
            ?: throw IllegalArgumentException("Album must have at least one artist")
}
