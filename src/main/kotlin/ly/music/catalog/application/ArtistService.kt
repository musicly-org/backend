package ly.music.catalog.application

import ly.music.catalog.domain.artist.ArtistEntity
import ly.music.catalog.domain.artist.ArtistRepository
import ly.music.catalog.configuration.cache.ARTIST_BY_ID
import ly.music.catalog.configuration.cache.ARTIST_PAGES
import ly.music.catalog.configuration.cache.ALBUMS_BY_ARTIST
import ly.music.catalog.configuration.cache.ALBUM_BY_ID
import ly.music.catalog.configuration.cache.ALBUM_VERSION_BY_ID
import ly.music.catalog.configuration.cache.ALBUM_VERSION_DEFAULT_BY_ALBUM
import ly.music.catalog.configuration.cache.ALBUM_VERSION_TRACKS_BY_VERSION
import ly.music.catalog.configuration.cache.ALBUM_VERSION_TRACK_BY_ID
import ly.music.catalog.configuration.cache.ALBUM_VERSIONS_BY_ALBUM
import ly.music.catalog.configuration.cache.SONGS_BY_ARTIST
import ly.music.catalog.configuration.cache.SONG_BY_ID
import ly.music.catalog.configuration.cache.SONG_VERSION_BY_ID
import ly.music.catalog.configuration.cache.SONG_VERSIONS_BY_SONG
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ArtistService(
    private val artistRepository: ArtistRepository,
) {
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = [ARTIST_PAGES], sync = true)
    fun findPage(pageable: Pageable): Page<ArtistEntity> = artistRepository.findAll(pageable)

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = [ARTIST_BY_ID], sync = true)
    fun findById(id: UUID): ArtistEntity =
        artistRepository.findById(id).orElseThrow { NotFoundException("Artist", id) }

    @Transactional
    @CacheEvict(cacheNames = [ARTIST_PAGES], allEntries = true)
    fun create(command: CreateArtistCommand): ArtistEntity {
        val normalizedName = ArtistEntity.normalizeName(command.name)
        require(!artistRepository.existsByNameIgnoreCase(normalizedName)) {
            "Artist already exists: $normalizedName"
        }

        return artistRepository.save(ArtistEntity(name = normalizedName))
    }

    @Transactional
    @CacheEvict(cacheNames = [ARTIST_PAGES, ARTIST_BY_ID], allEntries = true)
    fun rename(command: RenameArtistCommand): ArtistEntity {
        val artist = findById(command.id)
        val normalizedName = ArtistEntity.normalizeName(command.name)

        if (!artist.hasName(normalizedName)) {
            require(!artistRepository.existsByNameIgnoreCase(normalizedName)) {
                "Artist already exists: $normalizedName"
            }
        }

        artist.rename(normalizedName)
        return artist
    }

    @Transactional
    @CacheEvict(
        cacheNames = [
            ARTIST_PAGES,
            ARTIST_BY_ID,
            ALBUMS_BY_ARTIST,
            ALBUM_BY_ID,
            SONGS_BY_ARTIST,
            SONG_BY_ID,
            ALBUM_VERSIONS_BY_ALBUM,
            ALBUM_VERSION_BY_ID,
            ALBUM_VERSION_DEFAULT_BY_ALBUM,
            ALBUM_VERSION_TRACKS_BY_VERSION,
            ALBUM_VERSION_TRACK_BY_ID,
            SONG_VERSIONS_BY_SONG,
            SONG_VERSION_BY_ID,
        ],
        allEntries = true,
    )
    fun delete(id: UUID) {
        val artist = findById(id)
        artistRepository.delete(artist)
    }
}
