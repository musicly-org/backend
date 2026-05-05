package ly.music.catalog.application

import ly.music.catalog.domain.song.SongRepository
import ly.music.catalog.domain.songversion.SongVersionEntity
import ly.music.catalog.domain.songversion.SongVersionRepository
import ly.music.catalog.configuration.cache.SONG_VERSION_BY_ID
import ly.music.catalog.configuration.cache.SONG_VERSIONS_BY_SONG
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class SongVersionService(
    private val songVersionRepository: SongVersionRepository,
    private val songRepository: SongRepository,
) {
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = [SONG_VERSIONS_BY_SONG], sync = true)
    fun findBySong(
        songId: UUID,
        pageable: Pageable,
    ): Page<SongVersionEntity> {
        requireSongExists(songId)
        return songVersionRepository.findBySongId(songId, pageable)
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = [SONG_VERSION_BY_ID], sync = true)
    fun findById(id: UUID): SongVersionEntity =
        songVersionRepository.findById(id).orElseThrow { NotFoundException("SongVersion", id) }

    private fun requireSongExists(id: UUID) {
        if (!songRepository.existsById(id)) {
            throw NotFoundException("Song", id)
        }
    }
}
