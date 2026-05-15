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
@Table(name = "tracks", schema = "catalog")
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
    discNumber: Int = 1,
    trackNumber: Int,
) : BaseEntity() {
    @OneToOne(mappedBy = "track", fetch = FetchType.LAZY)
    var social: TrackSocialEntity? = null

    var title: String = normalizeTitle(title)
        protected set

    @Column(name = "duration_seconds")
    var durationSeconds: Int? = normalizeDurationSeconds(durationSeconds)
        protected set

    @Column(name = "released_at")
    var releasedAt: ReleasedAt? = releasedAt
        protected set

    @Column(name = "disc_number")
    var discNumber: Int = normalizeDiscNumber(discNumber)
        protected set

    @Column(name = "track_number")
    var trackNumber: Int = normalizeTrackNumber(trackNumber)
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
        this.durationSeconds = normalizeDurationSeconds(durationSeconds)
        this.releasedAt = releasedAt
        this.discNumber = normalizeDiscNumber(discNumber)
        this.trackNumber = normalizeTrackNumber(trackNumber)
    }

    fun hasTitle(title: String): Boolean = this.title.equals(normalizeTitle(title), ignoreCase = true)

    companion object {
        fun normalizeTitle(title: String): String = normalizeRequiredText(title, "Track title")

        private fun normalizeDurationSeconds(durationSeconds: Int?): Int? {
            require(durationSeconds == null || durationSeconds > 0) { "Track durationSeconds must be greater than 0" }
            return durationSeconds
        }

        private fun normalizeDiscNumber(discNumber: Int): Int {
            require(discNumber > 0) { "Track discNumber must be greater than 0" }
            return discNumber
        }

        private fun normalizeTrackNumber(trackNumber: Int): Int {
            require(trackNumber > 0) { "Track trackNumber must be greater than 0" }
            return trackNumber
        }
    }
}
