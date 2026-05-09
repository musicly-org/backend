package ly.music.catalog.interfaces.rest

import ly.music.catalog.interfaces.rest.album.AlbumController
import ly.music.catalog.interfaces.rest.album.AlbumForArtistController
import ly.music.catalog.interfaces.rest.artist.ArtistController
import ly.music.catalog.interfaces.rest.artist.ArtistForAlbumController
import ly.music.catalog.interfaces.rest.artist.ArtistForSongController
import ly.music.catalog.interfaces.rest.release.ReleaseController
import ly.music.catalog.interfaces.rest.release.ReleaseForAlbumController
import ly.music.catalog.interfaces.rest.song.SongController
import ly.music.catalog.interfaces.rest.song.SongForArtistController
import ly.music.catalog.interfaces.rest.track.TrackController
import ly.music.catalog.interfaces.rest.track.TrackForReleaseController
import ly.music.catalog.interfaces.rest.track.TrackForSongController
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

    fun album(id: UUID) = linkTo(methodOn(AlbumController::class.java).getAlbum(id)).withRel("album")

    fun albumArtists(id: UUID) =
        linkTo(
            methodOn(ArtistForAlbumController::class.java).getAlbumArtists(
                id,
                Pageable.unpaged(),
                pagedAssembler(),
            ),
        ).withRel("artists")

    fun releases(id: UUID) =
        linkTo(
            methodOn(ReleaseForAlbumController::class.java).getReleases(
                id,
                Pageable.unpaged(),
                pagedAssembler(),
            ),
        ).withRel("releases")

    fun release(id: UUID) = linkTo(methodOn(ReleaseController::class.java).getRelease(id)).withRel("release")

    fun releaseTracks(id: UUID) =
        linkTo(
            methodOn(TrackForReleaseController::class.java).getReleaseTracks(
                id,
                Pageable.unpaged(),
                pagedAssembler(),
            ),
        ).withRel("tracks")

    fun song(id: UUID) = linkTo(methodOn(SongController::class.java).getSong(id)).withRel("song")

    fun songArtists(id: UUID) =
        linkTo(
            methodOn(ArtistForSongController::class.java).getSongArtists(
                id,
                Pageable.unpaged(),
                pagedAssembler(),
            ),
        ).withRel("artists")

    fun songTracks(id: UUID) =
        linkTo(
            methodOn(TrackForSongController::class.java).getSongTracks(
                id,
                Pageable.unpaged(),
                pagedAssembler(),
            ),
        ).withRel("tracks")

    fun track(id: UUID) = linkTo(methodOn(TrackController::class.java).getTrack(id)).withRel("track")
}
