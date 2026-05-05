package ly.music.catalog.interfaces.rest.songversion

import ly.music.catalog.BackendControllerE2eTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SongVersionForSongControllerE2eTest : BackendControllerE2eTestSupport() {
    @Nested
    inner class GetSongVersions {
        @Test
        fun returnsPagedSongVersions() {
            val artist = createArtist()
            val song = createSong(artist = artist)
            val songVersion = createSongVersion(song = song)

            val body = getJson("/songs/${song.id}/versions")
            val item = embeddedItems(body).first()

            assertThat(body["page"]["totalElements"].asInt()).isEqualTo(1)
            assertNoId(item)
            assertThat(item["title"].asText()).isEqualTo(songVersion.title)
            assertThat(item["durationSeconds"].asInt()).isEqualTo(songVersion.durationSeconds)
            assertThat(item["releasedAt"].asText()).isEqualTo(songVersion.releasedAt?.value)
            assertThat(link(item, "self")).endsWith("/song-versions/${songVersion.id}")
            assertThat(link(item, "song")).endsWith("/songs/${song.id}")
        }
    }
}
