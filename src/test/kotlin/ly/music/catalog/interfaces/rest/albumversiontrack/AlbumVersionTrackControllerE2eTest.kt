package ly.music.catalog.interfaces.rest.albumversiontrack

import ly.music.catalog.BackendControllerE2eTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class AlbumVersionTrackControllerE2eTest : BackendControllerE2eTestSupport() {
    @Nested
    inner class GetTrack {
        @Test
        fun returnsTrack() {
            val artist = createArtist()
            val album = createAlbum(artist = artist)
            val albumVersion = createAlbumVersion(album = album)
            val song = createSong(artist = artist)
            val songVersion = createSongVersion(song = song)
            val track = createTrack(albumVersion = albumVersion, songVersion = songVersion)

            val body = getJson("/tracks/${track.id}")

            assertNoId(body)
            assertThat(body["discNumber"].asInt()).isEqualTo(track.discNumber)
            assertThat(body["trackNumber"].asInt()).isEqualTo(track.trackNumber)
            assertThat(link(body, "self")).endsWith("/tracks/${track.id}")
            assertThat(link(body, "album-version")).endsWith("/album-versions/${albumVersion.id}")
            assertThat(link(body, "song-version")).endsWith("/song-versions/${songVersion.id}")
        }
    }
}
