package ly.music.catalog.domain.albumversionsocial

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AlbumVersionSocialRepository : JpaRepository<AlbumVersionSocialEntity, UUID> {
    fun findBySpotifyId(spotifyId: String): AlbumVersionSocialEntity?
}
