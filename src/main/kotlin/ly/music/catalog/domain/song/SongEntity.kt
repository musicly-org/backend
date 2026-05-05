package ly.music.catalog.domain.song

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import ly.music.catalog.domain.BaseEntity
import ly.music.catalog.domain.artist.ArtistEntity
import ly.music.catalog.domain.normalizeRequiredText
import ly.music.catalog.domain.release.ReleasedAt
import ly.music.catalog.domain.songversion.SongVersionEntity

@Entity
@Table(name = "songs")
class SongEntity(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id")
    var artist: ArtistEntity,
    title: String,
    releasedAt: ReleasedAt? = null,
) : BaseEntity() {
    var title: String = normalizeTitle(title)
        protected set

    @Column(name = "released_at")
    var releasedAt: ReleasedAt? = releasedAt
        protected set

    @OneToMany(mappedBy = "song", cascade = [CascadeType.ALL], orphanRemoval = true)
    var versions: MutableSet<SongVersionEntity> = linkedSetOf()

    fun updateDetails(
        title: String,
        releasedAt: ReleasedAt?,
    ) {
        this.title = normalizeTitle(title)
        this.releasedAt = releasedAt
    }

    fun hasTitle(title: String): Boolean = this.title.equals(normalizeTitle(title), ignoreCase = true)

    companion object {
        fun normalizeTitle(title: String): String = normalizeRequiredText(title, "Song title")
    }
}
