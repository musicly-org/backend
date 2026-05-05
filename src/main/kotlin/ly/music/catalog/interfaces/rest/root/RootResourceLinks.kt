package ly.music.catalog.interfaces.rest.root

import ly.music.catalog.interfaces.rest.album.AlbumController
import ly.music.catalog.interfaces.rest.albumversion.AlbumVersionController
import ly.music.catalog.interfaces.rest.albumversiontrack.AlbumVersionTrackController
import ly.music.catalog.interfaces.rest.artist.ArtistController
import ly.music.catalog.interfaces.rest.pagedAssembler
import ly.music.catalog.interfaces.rest.song.SongController
import ly.music.catalog.interfaces.rest.songversion.SongVersionController
import org.springframework.data.domain.Pageable
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import java.util.UUID

internal object RootResourceLinks {
    private val UUID_TEMPLATE = UUID(0, 0)

    fun root() = linkTo(methodOn(ApiRootController::class.java).getRoot()).withSelfRel()

    fun artistsTemplate() =
        linkTo(
            methodOn(ArtistController::class.java).getArtists(
                Pageable.unpaged(),
                pagedAssembler(),
            ),
        ).withRel("artists")

    fun artistById() =
        linkTo(methodOn(ArtistController::class.java).getArtistById(UUID_TEMPLATE))
            .templated()
            .withRel("artist")

    fun albumTemplate() =
        linkTo(methodOn(AlbumController::class.java).getAlbum(UUID_TEMPLATE))
            .templated()
            .withRel("album")

    fun album(id: UUID) = linkTo(methodOn(AlbumController::class.java).getAlbum(id)).withSelfRel()

    fun albumVersion(id: UUID) = linkTo(methodOn(AlbumVersionController::class.java).getAlbumVersion(id)).withSelfRel()

    fun albumVersionTemplate() =
        linkTo(methodOn(AlbumVersionController::class.java).getAlbumVersion(UUID_TEMPLATE))
            .templated()
            .withRel("album-version")

    fun song(id: UUID) = linkTo(methodOn(SongController::class.java).getSong(id)).withSelfRel()

    fun songTemplate() =
        linkTo(methodOn(SongController::class.java).getSong(UUID_TEMPLATE))
            .templated()
            .withRel("song")

    fun songVersion(id: UUID) = linkTo(methodOn(SongVersionController::class.java).getSongVersion(id)).withSelfRel()

    fun songVersionTemplate() =
        linkTo(methodOn(SongVersionController::class.java).getSongVersion(UUID_TEMPLATE))
            .templated()
            .withRel("song-version")

    fun trackTemplate() =
        linkTo(methodOn(AlbumVersionTrackController::class.java).getTrack(UUID_TEMPLATE))
            .templated()
            .withRel("track")

    private fun WebMvcLinkBuilder.templated() =
        Link.of(
            this
                .toUri()
                .toString()
                .replace(UUID_TEMPLATE.toString(), "{id}"),
        )
}
