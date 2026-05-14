package ly.music.catalog.interfaces.rest.song

import ly.music.catalog.BackendControllerE2eTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SongControllerE2eTest : BackendControllerE2eTestSupport() {
    @Nested
    inner class GetSong {
        @Test
        fun existingSong_shouldReturnOk() {
            val primaryArtist = createArtist()
            val featuredArtist = createArtist(name = "Madonna")
            val song = createSong(artist = primaryArtist, artists = listOf(primaryArtist, featuredArtist))

            val body = getJson("/songs/${song.id}")

            assertNoId(body)
            assertThat(body["title"].asText()).isEqualTo(song.title)
            assertThat(body["releasedAt"].asText()).isEqualTo(song.releasedAt?.value)
            assertThat(link(body, "self")).endsWith("/songs/${song.id}")
            assertThat(link(body, "artists")).endsWith("/songs/${song.id}/artists")
            assertThat(link(body, "tracks")).endsWith("/songs/${song.id}/tracks")
        }
    }
}
