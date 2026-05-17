package ly.music.catalog.interfaces.rest.artist

import ly.music.catalog.BackendControllerE2eTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

class ArtistForSongControllerE2eTest : BackendControllerE2eTestSupport() {
    @Nested
    inner class GetSongArtists {
        @Test
        fun existingSongArtists_shouldReturnOk() {
            val primaryArtist = createArtist()
            val collaborator = createArtist(name = "Madonna")
            val song = createSong(artist = primaryArtist, artists = listOf(primaryArtist, collaborator))

            val body = getJson("/songs/${song.id}/artists")
            val items = embeddedItems(body)

            assertThat(body["page"]["totalElements"].asInt()).isEqualTo(2)
            assertThat(items.map { it["name"].asText() }).containsExactlyInAnyOrder(primaryArtist.name, collaborator.name)
            assertThat(items.map { link(it, "self") })
                .anySatisfy { assertThat(it).endsWith("/artists/${primaryArtist.id}") }
                .anySatisfy { assertThat(it).endsWith("/artists/${collaborator.id}") }
        }

        @Test
        fun missingSong_shouldReturnNotFound() {
            val missingSongId = UUID.randomUUID()

            val result = getResponse("/songs/$missingSongId/artists")

            status().isNotFound().match(result)
            assertThat(objectMapper.readTree(result.response.contentAsByteArray)["message"].asText()).isEqualTo("Song not found: $missingSongId")
        }
    }
}
