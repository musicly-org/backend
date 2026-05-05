package ly.music.catalog.domain.songversionsocial

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SongVersionSocialRepository : JpaRepository<SongVersionSocialEntity, UUID> {
    fun findBySongVersionId(songVersionId: UUID): SongVersionSocialEntity?
    fun findBySpotifyId(spotifyId: String): SongVersionSocialEntity?
}
