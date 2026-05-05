package ly.music.catalog.domain.songversionsocial

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import ly.music.catalog.domain.BaseEntity
import ly.music.catalog.domain.songversion.SongVersionEntity

@Entity
@Table(name = "song_version_social")
class SongVersionSocialEntity(
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "song_version_id", nullable = false)
    var songVersion: SongVersionEntity,
    @Column(name = "spotify_id", nullable = false, unique = true)
    var spotifyId: String,
) : BaseEntity()
