package ly.music.catalog.interfaces.rest.root

import ly.music.catalog.interfaces.rest.album.AlbumController
import ly.music.catalog.interfaces.rest.artist.ArtistController
import ly.music.catalog.interfaces.rest.release.ReleaseController
import ly.music.catalog.interfaces.rest.shared.pagedAssembler
import ly.music.catalog.interfaces.rest.song.SongController
import ly.music.catalog.interfaces.rest.track.TrackController
import org.springframework.data.domain.Pageable
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import java.util.UUID

object CatalogRootResourceLinks {
    private val UUID_TEMPLATE = UUID(0, 0)

    fun root() = linkTo(methodOn(CatalogRootController::class.java).getRoot()).withSelfRel()

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

    fun releaseTemplate() =
        linkTo(methodOn(ReleaseController::class.java).findReleaseById(UUID_TEMPLATE))
            .templated()
            .withRel("release")

    fun songTemplate() =
        linkTo(methodOn(SongController::class.java).getSong(UUID_TEMPLATE))
            .templated()
            .withRel("song")

    fun trackTemplate() =
        linkTo(methodOn(TrackController::class.java).getTrack(UUID_TEMPLATE))
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
