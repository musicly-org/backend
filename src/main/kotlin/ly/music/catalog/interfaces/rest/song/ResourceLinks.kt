package ly.music.catalog.interfaces.rest.song

import ly.music.catalog.interfaces.rest.album.AlbumController
import ly.music.catalog.interfaces.rest.album.AlbumForArtistController
import ly.music.catalog.interfaces.rest.artist.ArtistController
import ly.music.catalog.interfaces.rest.artist.ArtistForAlbumController
import ly.music.catalog.interfaces.rest.artist.ArtistForSongController
import ly.music.catalog.interfaces.rest.release.ReleaseController
import ly.music.catalog.interfaces.rest.release.ReleaseForAlbumController
import ly.music.catalog.interfaces.rest.shared.pagedAssembler
import ly.music.catalog.interfaces.rest.track.TrackController
import ly.music.catalog.interfaces.rest.track.TrackForReleaseController
import ly.music.catalog.interfaces.rest.track.TrackForSongController
import org.springframework.data.domain.Pageable
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder
import java.util.UUID

internal object ResourceLinks {
    fun artists() =
        WebMvcLinkBuilder
            .linkTo(
                WebMvcLinkBuilder.methodOn(ArtistController::class.java).getArtists(
                    Pageable.unpaged(),
                    pagedAssembler(),
                ),
            ).withRel("artists")

    fun artistAlbums(id: UUID) =
        WebMvcLinkBuilder
            .linkTo(
                WebMvcLinkBuilder.methodOn(AlbumForArtistController::class.java).getArtistAlbums(
                    id,
                    Pageable.unpaged(),
                    pagedAssembler(),
                ),
            ).withRel("albums")

    fun artistSongs(id: UUID) =
        WebMvcLinkBuilder
            .linkTo(
                WebMvcLinkBuilder.methodOn(SongForArtistController::class.java).getArtistSongs(
                    id,
                    Pageable.unpaged(),
                    pagedAssembler(),
                ),
            ).withRel("songs")

    fun album(id: UUID) =
        WebMvcLinkBuilder
            .linkTo(WebMvcLinkBuilder.methodOn(AlbumController::class.java).getAlbum(id))
            .withRel("album")

    fun albumArtists(id: UUID) =
        WebMvcLinkBuilder
            .linkTo(
                WebMvcLinkBuilder.methodOn(ArtistForAlbumController::class.java).getAlbumArtists(
                    id,
                    Pageable.unpaged(),
                    pagedAssembler(),
                ),
            ).withRel("artists")

    fun releases(id: UUID) =
        WebMvcLinkBuilder
            .linkTo(
                WebMvcLinkBuilder.methodOn(ReleaseForAlbumController::class.java).getReleases(
                    id,
                    Pageable.unpaged(),
                    pagedAssembler(),
                ),
            ).withRel("releases")

    fun release(id: UUID) =
        WebMvcLinkBuilder
            .linkTo(
                WebMvcLinkBuilder.methodOn(ReleaseController::class.java).getRelease(id),
            ).withRel("release")

    fun releaseTracks(id: UUID) =
        WebMvcLinkBuilder
            .linkTo(
                WebMvcLinkBuilder.methodOn(TrackForReleaseController::class.java).getReleaseTracks(
                    id,
                    Pageable.unpaged(),
                    pagedAssembler(),
                ),
            ).withRel("tracks")

    fun song(id: UUID) = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(SongController::class.java).getSong(id)).withRel("song")

    fun songArtists(id: UUID) =
        WebMvcLinkBuilder
            .linkTo(
                WebMvcLinkBuilder.methodOn(ArtistForSongController::class.java).getSongArtists(
                    id,
                    Pageable.unpaged(),
                    pagedAssembler(),
                ),
            ).withRel("artists")

    fun songTracks(id: UUID) =
        WebMvcLinkBuilder
            .linkTo(
                WebMvcLinkBuilder.methodOn(TrackForSongController::class.java).getSongTracks(
                    id,
                    Pageable.unpaged(),
                    pagedAssembler(),
                ),
            ).withRel("tracks")

    fun track(id: UUID) =
        WebMvcLinkBuilder
            .linkTo(WebMvcLinkBuilder.methodOn(TrackController::class.java).getTrack(id))
            .withRel("track")
}
