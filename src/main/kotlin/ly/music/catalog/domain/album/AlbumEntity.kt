package ly.music.catalog.domain.album

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import ly.music.catalog.domain.BaseEntity
import ly.music.catalog.domain.albumversion.AlbumVersionEntity
import ly.music.catalog.domain.artist.ArtistEntity
import ly.music.catalog.domain.normalizeRequiredText
import ly.music.catalog.domain.release.ReleasedAt

@Entity
@Table(name = "albums")
class AlbumEntity(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id")
    var artist: ArtistEntity,
    title: String,
    releasedAt: ReleasedAt? = null,
    imageUrl: String? = null,
) : BaseEntity() {
    var title: String = normalizeTitle(title)
        protected set

    @Column(name = "released_at")
    var releasedAt: ReleasedAt? = releasedAt
        protected set

    @Column(name = "image_url")
    var imageUrl: String? = imageUrl
        protected set

    @OneToMany(mappedBy = "album", cascade = [CascadeType.ALL], orphanRemoval = true)
    var versions: MutableSet<AlbumVersionEntity> = linkedSetOf()

    fun updateDetails(
        title: String,
        releasedAt: ReleasedAt?,
        imageUrl: String? = this.imageUrl,
    ) {
        this.title = normalizeTitle(title)
        this.releasedAt = releasedAt
        this.imageUrl = imageUrl
    }

    fun hasTitle(title: String): Boolean = this.title.equals(normalizeTitle(title), ignoreCase = true)

    companion object {
        fun normalizeTitle(title: String): String = normalizeRequiredText(title, "Album title")
    }
}
