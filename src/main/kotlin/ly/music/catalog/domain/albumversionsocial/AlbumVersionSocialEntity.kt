package ly.music.catalog.domain.albumversionsocial

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import ly.music.catalog.domain.BaseEntity
import ly.music.catalog.domain.albumversion.AlbumVersionEntity

@Entity
@Table(name = "album_version_social")
class AlbumVersionSocialEntity(
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "album_version_id", nullable = false)
    var albumVersion: AlbumVersionEntity,
    @Column(name = "spotify_id", nullable = false, unique = true)
    var spotifyId: String,
) : BaseEntity()
