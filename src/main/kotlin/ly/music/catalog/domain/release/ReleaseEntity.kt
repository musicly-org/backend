package ly.music.catalog.domain.release

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import ly.music.catalog.domain.BaseEntity
import ly.music.catalog.domain.album.AlbumEntity
import ly.music.catalog.domain.normalizeRequiredText
import ly.music.catalog.domain.track.TrackEntity

@Entity
@Table(name = "releases")
class ReleaseEntity(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id")
    var album: AlbumEntity,
    title: String,
    releasedAt: ReleasedAt? = null,
    imageUrl: String? = null,
    isDefault: Boolean = false,
) : BaseEntity() {
    var title: String = normalizeTitle(title)
        protected set

    @Column(name = "released_at")
    var releasedAt: ReleasedAt? = releasedAt
        protected set

    @Column(name = "image_url")
    var imageUrl: String? = imageUrl
        protected set

    @Column(name = "is_default", nullable = false)
    var isDefault: Boolean = isDefault
        protected set

    @OneToMany(mappedBy = "release", cascade = [CascadeType.ALL], orphanRemoval = true)
    var tracks: MutableSet<TrackEntity> = linkedSetOf()

    fun updateDetails(
        title: String,
        releasedAt: ReleasedAt?,
        imageUrl: String? = this.imageUrl,
    ) {
        this.title = normalizeTitle(title)
        this.releasedAt = releasedAt
        this.imageUrl = imageUrl
    }

    fun markAsDefault() {
        this.isDefault = true
    }

    fun hasTitle(title: String): Boolean = this.title.equals(normalizeTitle(title), ignoreCase = true)

    companion object {
        fun normalizeTitle(title: String): String = normalizeRequiredText(title, "Release title")
    }
}
