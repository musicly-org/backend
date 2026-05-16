package ly.music.catalog.interfaces.rest.artist

import ly.music.catalog.BackendControllerE2eTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ArtistControllerE2eTest : BackendControllerE2eTestSupport() {
    @Nested
    inner class Authorization {
        @Test
        fun wrongIssuerToken_shouldReturnUnauthorized() {
            val result =
                postJsonAuthorized(
                    "/artists",
                    mapOf("name" to "Portishead"),
                    issueTestToken(issuer = "foreign-environment"),
                )

            status().isUnauthorized().match(result)
            assertThat(artistRepository.findAll()).isEmpty()
        }
    }

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

        @Test
        fun duplicateSpotifyId_shouldReturnBadRequest() {
            val existingArtist = createArtist(name = "Portishead")
            createArtistSocial(artist = existingArtist, spotifyId = "spotify-artist-123")

            val result =
                postJsonAuthorized(
                    "/artists",
                    mapOf(
                        "name" to "Portishead Updated",
                        "imageUrl" to "https://example.test/new.jpg",
                        "spotifyId" to "spotify-artist-123",
                    ),
                    loginAsBootstrapAdmin(),
                )

            status().isBadRequest().match(result)
            assertThat(objectMapper.readTree(result.response.contentAsByteArray)["message"].asText()).isEqualTo(
                "Spotify artist already linked: spotify-artist-123",
            )
            assertThat(artistRepository.findAll()).hasSize(1)
            assertThat(artistRepository.findById(existingArtist.id).orElseThrow().name).isEqualTo("Portishead")
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

    @Nested
    inner class DeleteArtist {
        @Test
        fun referencedArtist_shouldReturnBadRequest() {
            val token = loginAsBootstrapAdmin()
            val artist = createArtist()
            createAlbum(artist = artist)
            createSong(artist = artist)

            val result = deleteAuthorized("/artists/${artist.id}", token)

            status().isBadRequest().match(result)
            assertThat(objectMapper.readTree(result.response.contentAsByteArray)["message"].asText()).isEqualTo(
                "Cannot delete an artist that is still referenced by albums or songs",
            )
            assertThat(artistRepository.findById(artist.id)).isPresent
        }

        @Test
        fun spotifyLinkedStandaloneArtist_shouldReturnNoContent() {
            val token = loginAsBootstrapAdmin()
            val artist = createArtist()
            createArtistSocial(artist = artist, spotifyId = "spotify-artist-delete")

            val result = deleteAuthorized("/artists/${artist.id}", token)

            status().isNoContent().match(result)
            assertThat(artistRepository.findById(artist.id)).isEmpty
            assertThat(artistSocialRepository.findBySpotifyId("spotify-artist-delete")).isNull()
        }
    }
}
