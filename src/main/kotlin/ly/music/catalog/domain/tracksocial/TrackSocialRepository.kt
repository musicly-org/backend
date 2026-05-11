package ly.music.catalog.domain.tracksocial

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TrackSocialRepository : JpaRepository<TrackSocialEntity, UUID> {
    fun findByTrackId(trackId: UUID): TrackSocialEntity?

    fun findBySpotifyId(spotifyId: String): TrackSocialEntity?
}
