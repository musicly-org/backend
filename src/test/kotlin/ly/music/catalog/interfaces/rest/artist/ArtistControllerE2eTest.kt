package ly.music.catalog.interfaces.rest.artist

import ly.music.catalog.BackendControllerE2eTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ArtistControllerE2eTest : BackendControllerE2eTestSupport() {
    @Nested
    inner class GetArtists {
        @Test
        fun existingArtists_shouldReturnOk() {
            val artist = createArtist()

            val body = getJson("/artists")
            val item = embeddedItems(body).first()

            assertThat(body["page"]["totalElements"].asInt()).isEqualTo(1)
            assertNoId(item)
            assertThat(item["name"].asText()).isEqualTo(artist.name)
            assertThat(item["imageUrl"].asText()).isEqualTo(artist.imageUrl)
            assertThat(link(item, "self")).endsWith("/artists/${artist.id}")
            assertThat(link(item, "albums")).endsWith("/artists/${artist.id}/albums")
            assertThat(link(item, "songs")).endsWith("/artists/${artist.id}/songs")
        }
    }

    @Nested
    inner class GetArtist {
        @Test
        fun existingArtist_shouldReturnOk() {
            val artist = createArtist()

            val body = getJson("/artists/${artist.id}")

            assertNoId(body)
            assertThat(body["name"].asText()).isEqualTo(artist.name)
            assertThat(body["imageUrl"].asText()).isEqualTo(artist.imageUrl)
            assertThat(link(body, "self")).endsWith("/artists/${artist.id}")
            assertThat(link(body, "albums")).endsWith("/artists/${artist.id}/albums")
            assertThat(link(body, "songs")).endsWith("/artists/${artist.id}/songs")
        }
    }

    @Nested
    inner class CreateArtist {
        @Test
        fun anonymousRequest_shouldReturnUnauthorized() {
            val result = postJson("/artists", mapOf("name" to "Portishead"))

            status().isUnauthorized().match(result)
            assertThat(artistRepository.findAll()).isEmpty()
        }

        @Test
        fun adminRequest_shouldReturnCreated() {
            val result = postJsonAuthorized("/artists", mapOf("name" to "Portishead"), loginAsBootstrapAdmin())

            status().isCreated().match(result)

            val body = objectMapper.readTree(result.response.contentAsByteArray)
            val createdArtist = artistRepository.findAll().single()

            assertNoId(body)
            assertThat(createdArtist.name).isEqualTo("Portishead")
            assertThat(result.response.getHeader("Location")).endsWith("/artists/${createdArtist.id}")
            assertThat(link(body, "self")).endsWith("/artists/${createdArtist.id}")
            assertThat(link(body, "albums")).endsWith("/artists/${createdArtist.id}/albums")
            assertThat(link(body, "songs")).endsWith("/artists/${createdArtist.id}/songs")
        }

        @Test
        fun adminRequest_withSameNameAsExistingArtist_shouldCreateDistinctArtist() {
            createArtist(name = "Breathe")

            val result = postJsonAuthorized("/artists", mapOf("name" to "  Breathe  "), loginAsBootstrapAdmin())

            status().isCreated().match(result)
            assertThat(artistRepository.findAll()).hasSize(2)
            assertThat(artistRepository.findAll().map { it.name }).containsExactlyInAnyOrder("Breathe", "Breathe")
        }
    }

    @Nested
    inner class UpdateArtist {
        @Test
        fun adminRequest_renamingToSharedName_shouldReturnOk() {
            val existing = createArtist(name = "Low")
            val renamed = createArtist(name = "Low Roar")

            val result =
                putJsonAuthorized(
                    "/artists/${renamed.id}",
                    mapOf("name" to " Low "),
                    loginAsBootstrapAdmin(),
                )

            status().isOk().match(result)

            artistRepository.findById(renamed.id).orElseThrow().also { updated ->
                assertThat(updated.name).isEqualTo("Low")
            }
            assertThat(artistRepository.findById(existing.id).orElseThrow().name).isEqualTo("Low")
        }
    }
}
