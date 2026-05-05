package ly.music.catalog.domain.albumversiontrack

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import ly.music.catalog.domain.BaseEntity
import ly.music.catalog.domain.albumversion.AlbumVersionEntity
import ly.music.catalog.domain.songversion.SongVersionEntity

@Entity
@Table(name = "album_version_tracks")
class AlbumVersionTrackEntity(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_version_id")
    var albumVersion: AlbumVersionEntity,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "song_version_id")
    var songVersion: SongVersionEntity,
    @Column(name = "disc_number")
    var discNumber: Int = 1,
    @Column(name = "track_number")
    var trackNumber: Int,
) : BaseEntity()
