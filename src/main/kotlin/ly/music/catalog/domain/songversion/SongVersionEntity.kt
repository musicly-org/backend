package ly.music.catalog.domain.songversion

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import ly.music.catalog.domain.BaseEntity
import ly.music.catalog.domain.albumversiontrack.AlbumVersionTrackEntity
import ly.music.catalog.domain.normalizeRequiredText
import ly.music.catalog.domain.release.ReleasedAt
import ly.music.catalog.domain.song.SongEntity

@Entity
@Table(name = "song_versions")
class SongVersionEntity(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "song_id")
    var song: SongEntity,
    title: String,
    durationSeconds: Int? = null,
    releasedAt: ReleasedAt? = null,
) : BaseEntity() {
    var title: String = normalizeTitle(title)
        protected set

    @Column(name = "duration_seconds")
    var durationSeconds: Int? = durationSeconds
        protected set

    @Column(name = "released_at")
    var releasedAt: ReleasedAt? = releasedAt
        protected set

    @OneToMany(mappedBy = "songVersion", cascade = [CascadeType.ALL], orphanRemoval = true)
    var albumTracks: MutableSet<AlbumVersionTrackEntity> = linkedSetOf()

    fun updateDetails(
        title: String,
        durationSeconds: Int?,
        releasedAt: ReleasedAt?,
    ) {
        this.title = normalizeTitle(title)
        this.durationSeconds = durationSeconds
        this.releasedAt = releasedAt
    }

    fun hasTitle(title: String): Boolean = this.title.equals(normalizeTitle(title), ignoreCase = true)

    companion object {
        fun normalizeTitle(title: String): String = normalizeRequiredText(title, "Song version title")
    }
}
