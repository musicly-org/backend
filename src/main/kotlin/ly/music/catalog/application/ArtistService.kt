package ly.music.catalog.application

import ly.music.catalog.configuration.cache.ALBUMS_BY_ARTIST
import ly.music.catalog.configuration.cache.ALBUM_BY_ID
import ly.music.catalog.configuration.cache.ARTIST_BY_ID
import ly.music.catalog.configuration.cache.ARTIST_PAGES
import ly.music.catalog.configuration.cache.RELEASES_BY_ALBUM
import ly.music.catalog.configuration.cache.RELEASE_BY_ID
import ly.music.catalog.configuration.cache.RELEASE_DEFAULT_BY_ALBUM
import ly.music.catalog.configuration.cache.SONGS_BY_ARTIST
import ly.music.catalog.configuration.cache.SONG_BY_ID
import ly.music.catalog.configuration.cache.TRACKS_BY_RELEASE
import ly.music.catalog.configuration.cache.TRACKS_BY_SONG
import ly.music.catalog.configuration.cache.TRACK_BY_ID
import ly.music.catalog.domain.album.AlbumRepository
import ly.music.catalog.domain.artist.ArtistEntity
import ly.music.catalog.domain.artist.ArtistRepository
import ly.music.catalog.domain.artistsocial.ArtistSocialEntity
import ly.music.catalog.domain.artistsocial.ArtistSocialRepository
import ly.music.catalog.domain.song.SongRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ArtistService(
    private val artistRepository: ArtistRepository,
    private val artistSocialRepository: ArtistSocialRepository,
    private val albumRepository: AlbumRepository,
    private val songRepository: SongRepository,
) {
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = [ARTIST_PAGES], sync = true)
    fun findPage(pageable: Pageable): Page<ArtistEntity> = artistRepository.findAll(pageable)

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = [ARTIST_BY_ID], sync = true)
    fun findById(id: UUID): ArtistEntity =
        artistRepository.findById(id).orElseThrow { NotFoundException("Artist", id) }

    @Transactional(readOnly = true)
    fun findByAlbum(
        albumId: UUID,
        pageable: Pageable,
    ): Page<ArtistEntity> {
        requireAlbumExists(albumId)
        return artistRepository.findDistinctByAlbumsId(albumId, pageable)
    }

    @Transactional(readOnly = true)
    fun findBySong(
        songId: UUID,
        pageable: Pageable,
    ): Page<ArtistEntity> {
        requireSongExists(songId)
        return artistRepository.findDistinctBySongsId(songId, pageable)
    }

    @Transactional
    @CacheEvict(cacheNames = [ARTIST_PAGES], allEntries = true)
    fun create(command: CreateArtistCommand): ArtistEntity {
        val normalizedName = ArtistEntity.normalizeName(command.name)

        command.spotifyId?.let { spotifyId ->
            artistRepository.findBySocialSpotifyId(spotifyId)?.let { existing ->
                existing.updateDetails(normalizedName, command.imageUrl ?: existing.imageUrl)
                syncSocial(existing, spotifyId)
                return existing
            }
        }

        require(!artistRepository.existsByNameIgnoreCase(normalizedName)) {
            "Artist already exists: $normalizedName"
        }

        val artist = artistRepository.save(ArtistEntity(name = normalizedName, imageUrl = command.imageUrl))
        syncSocial(artist, command.spotifyId)
        return artist
    }

    @Transactional
    @CacheEvict(cacheNames = [ARTIST_PAGES, ARTIST_BY_ID], allEntries = true)
    fun update(command: UpdateArtistCommand): ArtistEntity {
        val artist = findById(command.id)
        val normalizedName = ArtistEntity.normalizeName(command.name)

        if (!artist.hasName(normalizedName)) {
            require(!artistRepository.existsByNameIgnoreCase(normalizedName)) {
                "Artist already exists: $normalizedName"
            }
        }

        command.spotifyId?.let { spotifyId ->
            artistRepository.findBySocialSpotifyId(spotifyId)?.let { existing ->
                require(existing.id == artist.id) { "Spotify artist already linked: $spotifyId" }
            }
        }

        artist.updateDetails(normalizedName, command.imageUrl)
        syncSocial(artist, command.spotifyId)
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
            RELEASES_BY_ALBUM,
            RELEASE_BY_ID,
            RELEASE_DEFAULT_BY_ALBUM,
            TRACKS_BY_RELEASE,
            TRACK_BY_ID,
            TRACKS_BY_SONG,
        ],
        allEntries = true,
    )
    fun delete(id: UUID) {
        val artist = findById(id)
        artistRepository.delete(artist)
    }

    private fun requireAlbumExists(id: UUID) {
        if (!albumRepository.existsById(id)) {
            throw NotFoundException("Album", id)
        }
    }

    private fun requireSongExists(id: UUID) {
        if (!songRepository.existsById(id)) {
            throw NotFoundException("Song", id)
        }
    }

    private fun syncSocial(
        artist: ArtistEntity,
        spotifyId: String?,
    ) {
        if (spotifyId.isNullOrBlank()) {
            return
        }

        val existing = artistSocialRepository.findBySpotifyId(spotifyId)
        require(existing == null || existing.artist.id == artist.id) { "Spotify artist already linked: $spotifyId" }

        val social = artist.social
        if (social == null) {
            artistSocialRepository.save(ArtistSocialEntity(artist = artist, spotifyId = spotifyId))
        } else {
            social.spotifyId = spotifyId
        }
    }
}
