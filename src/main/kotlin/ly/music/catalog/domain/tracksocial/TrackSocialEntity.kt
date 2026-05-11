package ly.music.catalog.domain.tracksocial

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import ly.music.catalog.domain.BaseEntity
import ly.music.catalog.domain.track.TrackEntity

@Entity
@Table(name = "track_social")
class TrackSocialEntity(
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "track_id", nullable = false, unique = true)
    var track: TrackEntity,
    @Column(name = "spotify_id", nullable = false, unique = true)
    var spotifyId: String,
) : BaseEntity()
