package ly.music.catalog.domain.track

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import ly.music.catalog.domain.BaseEntity
import ly.music.catalog.domain.release.ReleaseEntity
import ly.music.catalog.domain.normalizeRequiredText
import ly.music.catalog.domain.release.ReleasedAt
import ly.music.catalog.domain.song.SongEntity
import ly.music.catalog.domain.tracksocial.TrackSocialEntity

@Entity
@Table(name = "tracks")
class TrackEntity(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "song_id")
    var song: SongEntity,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "release_id")
    var release: ReleaseEntity,
    title: String,
    @Column(name = "image_url")
    var imageUrl: String? = null,
    durationSeconds: Int? = null,
    releasedAt: ReleasedAt? = null,
    @Column(name = "disc_number")
    var discNumber: Int = 1,
    @Column(name = "track_number")
    var trackNumber: Int,
) : BaseEntity() {
    @OneToOne(mappedBy = "track", fetch = FetchType.LAZY)
    var social: TrackSocialEntity? = null

    var title: String = normalizeTitle(title)
        protected set

    @Column(name = "duration_seconds")
    var durationSeconds: Int? = durationSeconds
        protected set

    @Column(name = "released_at")
    var releasedAt: ReleasedAt? = releasedAt
        protected set

    fun updateDetails(
        title: String,
        imageUrl: String?,
        durationSeconds: Int?,
        releasedAt: ReleasedAt?,
        discNumber: Int,
        trackNumber: Int,
    ) {
        this.title = normalizeTitle(title)
        this.imageUrl = imageUrl
        this.durationSeconds = durationSeconds
        this.releasedAt = releasedAt
        this.discNumber = discNumber
        this.trackNumber = trackNumber
    }

    fun hasTitle(title: String): Boolean = this.title.equals(normalizeTitle(title), ignoreCase = true)

    companion object {
        fun normalizeTitle(title: String): String = normalizeRequiredText(title, "Track title")
    }
}
