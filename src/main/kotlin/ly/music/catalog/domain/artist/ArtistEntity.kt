package ly.music.catalog.domain.artist

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import ly.music.catalog.domain.BaseEntity
import ly.music.catalog.domain.album.AlbumEntity
import ly.music.catalog.domain.normalizeRequiredText
import ly.music.catalog.domain.song.SongEntity

@Entity
@Table(name = "artists")
class ArtistEntity(
    name: String,
    imageUrl: String? = null,
) : BaseEntity() {
    var name: String = normalizeName(name)
        protected set

    @Column(name = "image_url")
    var imageUrl: String? = imageUrl
        protected set

    @ManyToMany(mappedBy = "artists")
    var albums: MutableSet<AlbumEntity> = linkedSetOf()

    @ManyToMany(mappedBy = "artists")
    var songs: MutableSet<SongEntity> = linkedSetOf()

    fun rename(name: String) {
        this.name = normalizeName(name)
    }

    fun updateImageUrl(imageUrl: String?) {
        this.imageUrl = imageUrl
    }

    fun hasName(name: String): Boolean = this.name.equals(normalizeName(name), ignoreCase = true)

    companion object {
        fun normalizeName(name: String): String = normalizeRequiredText(name, "Artist name")
    }
}
