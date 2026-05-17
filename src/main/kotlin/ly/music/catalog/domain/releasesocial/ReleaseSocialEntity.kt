package ly.music.catalog.domain.releasesocial

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import ly.music.catalog.domain.BaseEntity
import ly.music.catalog.domain.release.ReleaseEntity

@Entity
@Table(name = "release_social", schema = "catalog")
class ReleaseSocialEntity(
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "release_id", nullable = false)
    var release: ReleaseEntity,
    @Column(name = "spotify_id", nullable = false, unique = true)
    var spotifyId: String,
) : BaseEntity()
