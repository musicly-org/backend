package ly.music.catalog.domain.artist

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.ManyToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import ly.music.catalog.domain.BaseEntity
import ly.music.catalog.domain.album.AlbumEntity
import ly.music.catalog.domain.normalizeRequiredText
import ly.music.catalog.domain.artistsocial.ArtistSocialEntity
import ly.music.catalog.domain.song.SongEntity

@Entity
@Table(name = "artists", schema = "catalog")
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

    @OneToOne(mappedBy = "artist")
    var social: ArtistSocialEntity? = null

    fun updateDetails(
        name: String,
        imageUrl: String? = this.imageUrl,
    ) {
        this.name = normalizeName(name)
        this.imageUrl = imageUrl
    }

    companion object {
        fun normalizeName(name: String): String = normalizeRequiredText(name, "Artist name")
    }
}
