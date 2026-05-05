package ly.music.catalog.interfaces.rest.albumversiontrack

import ly.music.catalog.BackendControllerE2eTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class AlbumVersionTrackForAlbumVersionControllerE2eTest : BackendControllerE2eTestSupport() {
    @Nested
    inner class GetAlbumVersionTracks {
        @Test
        fun returnsPagedTracks() {
            val artist = createArtist()
            val album = createAlbum(artist = artist)
            val albumVersion = createAlbumVersion(album = album)
            val song = createSong(artist = artist)
            val songVersion = createSongVersion(song = song)
            val track = createTrack(albumVersion = albumVersion, songVersion = songVersion)

            val body = getJson("/album-versions/${albumVersion.id}/tracks")
            val item = embeddedItems(body).first()

            assertThat(body["page"]["totalElements"].asInt()).isEqualTo(1)
            assertNoId(item)
            assertThat(item["discNumber"].asInt()).isEqualTo(track.discNumber)
            assertThat(item["trackNumber"].asInt()).isEqualTo(track.trackNumber)
            assertThat(link(item, "self")).endsWith("/tracks/${track.id}")
            assertThat(link(item, "album-version")).endsWith("/album-versions/${albumVersion.id}")
            assertThat(link(item, "song-version")).endsWith("/song-versions/${songVersion.id}")
        }
    }
}
