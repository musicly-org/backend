package ly.music.catalog.application

import ly.music.catalog.domain.artist.ArtistRepository
import ly.music.catalog.domain.song.SongEntity
import ly.music.catalog.domain.song.SongRepository
import ly.music.catalog.configuration.cache.SONGS_BY_ARTIST
import ly.music.catalog.configuration.cache.SONG_BY_ID
import ly.music.catalog.configuration.cache.SONG_VERSION_BY_ID
import ly.music.catalog.configuration.cache.SONG_VERSIONS_BY_SONG
import ly.music.catalog.configuration.cache.ALBUM_VERSION_TRACKS_BY_VERSION
import ly.music.catalog.configuration.cache.ALBUM_VERSION_TRACK_BY_ID
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class SongService(
    private val songRepository: SongRepository,
    private val artistRepository: ArtistRepository,
) {
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = [SONGS_BY_ARTIST], sync = true)
    fun findByArtist(
        artistId: UUID,
        pageable: Pageable,
    ): Page<SongEntity> {
        requireArtistExists(artistId)
        return songRepository.findByArtistId(artistId, pageable)
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = [SONG_BY_ID], sync = true)
    fun findById(id: UUID): SongEntity =
        songRepository.findById(id).orElseThrow { NotFoundException("Song", id) }

    @Transactional
    @CacheEvict(cacheNames = [SONGS_BY_ARTIST], allEntries = true)
    fun create(command: CreateSongCommand): SongEntity {
        val artist = artistRepository.findById(command.artistId).orElseThrow {
            NotFoundException("Artist", command.artistId)
        }
        val normalizedTitle = SongEntity.normalizeTitle(command.title)

        require(!songRepository.existsByArtistIdAndTitleIgnoreCase(command.artistId, normalizedTitle)) {
            "Song already exists for artist: $normalizedTitle"
        }

        return songRepository.save(
            SongEntity(
                artist = artist,
                title = normalizedTitle,
                releasedAt = command.releasedAt,
            ),
        )
    }

    @Transactional
    @CacheEvict(cacheNames = [SONGS_BY_ARTIST, SONG_BY_ID], allEntries = true)
    fun rename(command: RenameSongCommand): SongEntity {
        val song = findById(command.id)
        val normalizedTitle = SongEntity.normalizeTitle(command.title)

        if (!song.hasTitle(normalizedTitle)) {
            require(!songRepository.existsByArtistIdAndTitleIgnoreCase(song.artist.id, normalizedTitle)) {
                "Song already exists for artist: $normalizedTitle"
            }
        }

        song.updateDetails(normalizedTitle, command.releasedAt)
        return song
    }

    @Transactional
    @CacheEvict(
        cacheNames = [
            SONGS_BY_ARTIST,
            SONG_BY_ID,
            SONG_VERSIONS_BY_SONG,
            SONG_VERSION_BY_ID,
            ALBUM_VERSION_TRACKS_BY_VERSION,
            ALBUM_VERSION_TRACK_BY_ID,
        ],
        allEntries = true,
    )
    fun delete(id: UUID) {
        val song = findById(id)
        songRepository.delete(song)
    }

    private fun requireArtistExists(id: UUID) {
        if (!artistRepository.existsById(id)) {
            throw NotFoundException("Artist", id)
        }
    }
}
