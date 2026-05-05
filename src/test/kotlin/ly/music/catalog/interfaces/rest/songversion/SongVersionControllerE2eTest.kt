package ly.music.catalog.interfaces.rest.songversion

import ly.music.catalog.BackendControllerE2eTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SongVersionControllerE2eTest : BackendControllerE2eTestSupport() {
    @Nested
    inner class GetSongVersion {
        @Test
        fun returnsSongVersion() {
            val artist = createArtist()
            val song = createSong(artist = artist)
            val songVersion = createSongVersion(song = song)

            val body = getJson("/song-versions/${songVersion.id}")

            assertNoId(body)
            assertThat(body["title"].asText()).isEqualTo(songVersion.title)
            assertThat(body["durationSeconds"].asInt()).isEqualTo(songVersion.durationSeconds)
            assertThat(body["releasedAt"].asText()).isEqualTo(songVersion.releasedAt?.value)
            assertThat(link(body, "self")).endsWith("/song-versions/${songVersion.id}")
            assertThat(link(body, "song")).endsWith("/songs/${song.id}")
        }
    }
}
