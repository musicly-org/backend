package ly.music.catalog.application

import ly.music.catalog.configuration.cache.SONGS_BY_ARTIST
import ly.music.catalog.configuration.cache.SONG_BY_ID
import ly.music.catalog.configuration.cache.TRACKS_BY_RELEASE
import ly.music.catalog.configuration.cache.TRACKS_BY_SONG
import ly.music.catalog.configuration.cache.TRACK_BY_ID
import ly.music.catalog.domain.artist.ArtistRepository
import ly.music.catalog.domain.song.SongEntity
import ly.music.catalog.domain.song.SongRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
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
        return songRepository.findDistinctByArtistsId(artistId, pageable)
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = [SONG_BY_ID], sync = true)
    fun findById(id: UUID): SongEntity =
        songRepository.findByIdOrThrow(id)

    @Transactional
    @CacheEvict(cacheNames = [SONGS_BY_ARTIST], allEntries = true)
    fun create(command: CreateSongCommand): SongEntity {
        val artists = resolveArtists(command.artistIds)
        val normalizedTitle = SongEntity.normalizeTitle(command.title)
        require(!songRepository.existsByArtistsIdAndTitleIgnoreCase(artists.first().id, normalizedTitle)) {
            "Song already exists for artist: $normalizedTitle"
        }

        return songRepository.save(
            SongEntity(
                title = normalizedTitle,
                releasedAt = command.releasedAt,
            ).also { it.artists.addAll(artists) },
        )
    }

    @Transactional
    @CacheEvict(cacheNames = [SONGS_BY_ARTIST, SONG_BY_ID], allEntries = true)
    fun update(command: UpdateSongCommand): SongEntity {
        val song = findById(command.id)
        val artists = resolveArtists(command.artistIds)
        val normalizedTitle = SongEntity.normalizeTitle(command.title)

        if (!song.hasTitle(normalizedTitle) || song.artists.map { it.id }.toSet() != command.artistIds) {
            require(!songRepository.existsByArtistsIdAndTitleIgnoreCaseAndIdNot(artists.first().id, normalizedTitle, song.id)) {
                "Song already exists for artist: $normalizedTitle"
            }
        }

        song.updateDetails(normalizedTitle, command.releasedAt)
        song.artists.clear()
        song.artists.addAll(artists)
        return song
    }

    @Transactional
    @CacheEvict(
        cacheNames = [
            SONGS_BY_ARTIST,
            SONG_BY_ID,
            TRACKS_BY_SONG,
            TRACK_BY_ID,
            TRACKS_BY_RELEASE,
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

    private fun resolveArtists(ids: Set<UUID>) =
        ids
            .takeIf { it.isNotEmpty() }
            ?.let { artistRepository.findAllById(it).toCollection(linkedSetOf()) }
            ?.also { artists ->
                val missingIds = ids - artists.map { it.id }.toSet()
                require(missingIds.isEmpty()) { "Artists not found: ${missingIds.joinToString(",")}" }
            }
            ?: throw IllegalArgumentException("Song must have at least one artist")
}
