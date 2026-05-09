package ly.music.catalog.domain.releasesocial

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ReleaseSocialRepository : JpaRepository<ReleaseSocialEntity, UUID> {
    fun findBySpotifyId(spotifyId: String): ReleaseSocialEntity?
}
