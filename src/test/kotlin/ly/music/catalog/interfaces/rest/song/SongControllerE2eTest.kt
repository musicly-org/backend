package ly.music.catalog.interfaces.rest.song

import ly.music.catalog.BackendControllerE2eTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SongControllerE2eTest : BackendControllerE2eTestSupport() {
    @Nested
    inner class GetSong {
        @Test
        fun returnsSong() {
            val artist = createArtist()
            val song = createSong(artist = artist)

            val body = getJson("/songs/${song.id}")

            assertNoId(body)
            assertThat(body["title"].asText()).isEqualTo(song.title)
            assertThat(body["releasedAt"].asText()).isEqualTo(song.releasedAt?.value)
            assertThat(link(body, "self")).endsWith("/songs/${song.id}")
            assertThat(link(body, "artist")).endsWith("/artists/${artist.id}")
            assertThat(link(body, "song-versions")).endsWith("/songs/${song.id}/versions")
        }
    }
}
