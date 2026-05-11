package ly.music.catalog.domain.artistsocial

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import ly.music.catalog.domain.BaseEntity
import ly.music.catalog.domain.artist.ArtistEntity

@Entity
@Table(name = "artist_social", schema = "catalog")
class ArtistSocialEntity(
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "artist_id", nullable = false)
    var artist: ArtistEntity,
    @Column(name = "spotify_id", nullable = false, unique = true)
    var spotifyId: String,
) : BaseEntity()
