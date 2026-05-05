package ly.music.catalog.interfaces.rest

import ly.music.catalog.interfaces.rest.album.AlbumController
import ly.music.catalog.interfaces.rest.album.AlbumForArtistController
import ly.music.catalog.interfaces.rest.albumversion.AlbumVersionController
import ly.music.catalog.interfaces.rest.albumversion.AlbumVersionForAlbumController
import ly.music.catalog.interfaces.rest.albumversiontrack.AlbumVersionTrackForAlbumVersionController
import ly.music.catalog.interfaces.rest.artist.ArtistController
import ly.music.catalog.interfaces.rest.song.SongController
import ly.music.catalog.interfaces.rest.song.SongForArtistController
import ly.music.catalog.interfaces.rest.songversion.SongVersionController
import ly.music.catalog.interfaces.rest.songversion.SongVersionForSongController
import org.springframework.data.domain.Pageable
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import java.util.UUID

internal object ResourceLinks {
    fun artists() =
        linkTo(
            methodOn(ArtistController::class.java).getArtists(
                Pageable.unpaged(),
                pagedAssembler(),
            ),
        ).withRel("artists")

    fun artistById(id: UUID) = linkTo(methodOn(ArtistController::class.java).getArtistById(id)).withSelfRel()

    fun artistAlbums(id: UUID) =
        linkTo(
            methodOn(AlbumForArtistController::class.java).getArtistAlbums(
                id,
                Pageable.unpaged(),
                pagedAssembler(),
            ),
        ).withRel("albums")

    fun artistSongs(id: UUID) =
        linkTo(
            methodOn(SongForArtistController::class.java).getArtistSongs(
                id,
                Pageable.unpaged(),
                pagedAssembler(),
            ),
        ).withRel("songs")

    fun album(id: UUID) = linkTo(methodOn(AlbumController::class.java).getAlbum(id)).withSelfRel()

    fun albumVersions(id: UUID) =
        linkTo(
            methodOn(AlbumVersionForAlbumController::class.java).getAlbumVersions(
                id,
                Pageable.unpaged(),
                pagedAssembler(),
            ),
        ).withRel("album-versions")

    fun albumVersion(id: UUID) = linkTo(methodOn(AlbumVersionController::class.java).getAlbumVersion(id)).withSelfRel()

    fun albumVersionTracks(id: UUID) =
        linkTo(
            methodOn(AlbumVersionTrackForAlbumVersionController::class.java).getAlbumVersionTracks(
                id,
                Pageable.unpaged(),
                pagedAssembler(),
            ),
        ).withRel("tracks")

    fun song(id: UUID) = linkTo(methodOn(SongController::class.java).getSong(id)).withSelfRel()

    fun songVersions(id: UUID) =
        linkTo(
            methodOn(SongVersionForSongController::class.java).getSongVersions(
                id,
                Pageable.unpaged(),
                pagedAssembler(),
            ),
        ).withRel("song-versions")

    fun songVersion(id: UUID) = linkTo(methodOn(SongVersionController::class.java).getSongVersion(id)).withSelfRel()
}
