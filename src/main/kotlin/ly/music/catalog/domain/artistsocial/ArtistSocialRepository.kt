package ly.music.catalog.domain.artistsocial

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ArtistSocialRepository : JpaRepository<ArtistSocialEntity, UUID> {
    fun findBySpotifyId(spotifyId: String): ArtistSocialEntity?
}
