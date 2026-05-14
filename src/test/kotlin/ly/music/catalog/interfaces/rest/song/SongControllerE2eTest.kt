package ly.music.catalog.interfaces.rest.song

import ly.music.catalog.BackendControllerE2eTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

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

    @Nested
    inner class CreateSong {
        @Test
        fun adminRequest_shouldReturnCreated() {
            val artist = createArtist()

            val result =
                postJsonAuthorized(
                    "/songs",
                    mapOf(
                        "title" to "Teardrop",
                        "releasedAt" to "1998-04",
                        "_links" to
                            mapOf(
                                "artists" to
                                    listOf(
                                        mapOf("href" to "http://localhost:8080/artists/${artist.id}"),
                                    ),
                            ),
                    ),
                    loginAsBootstrapAdmin(),
                )

            status().isCreated().match(result)
            val body = objectMapper.readTree(result.response.contentAsByteArray)
            val createdSong = songRepository.findAll().single()
            assertNoId(body)
            assertThat(createdSong.title).isEqualTo("Teardrop")
            assertThat(body["title"].asText()).isEqualTo("Teardrop")
            assertThat(link(body, "self")).endsWith("/songs/${createdSong.id}")
            assertThat(link(body, "artists")).endsWith("/songs/${createdSong.id}/artists")
            assertThat(link(body, "tracks")).endsWith("/songs/${createdSong.id}/tracks")
        }

        @Test
        fun collaboratorConflict_shouldReturnBadRequest() {
            val primaryArtist = createArtist(name = "Massive Attack")
            val collaborator = createArtist(name = "Elizabeth Fraser")
            createSong(artist = collaborator, title = "Teardrop")

            val result =
                postJsonAuthorized(
                    "/songs",
                    mapOf(
                        "title" to "Teardrop",
                        "_links" to
                            mapOf(
                                "artists" to
                                    listOf(
                                        mapOf("href" to "http://localhost:8080/artists/${primaryArtist.id}"),
                                        mapOf("href" to "http://localhost:8080/artists/${collaborator.id}"),
                                    ),
                            ),
                    ),
                    loginAsBootstrapAdmin(),
                )

            status().isBadRequest().match(result)
            assertThat(objectMapper.readTree(result.response.contentAsByteArray)["message"].asText()).isEqualTo(
                "Song already exists for artist: Teardrop",
            )
        }
    }

    @Nested
    inner class UpdateSong {
        @Test
        fun conflictingTitleForSameArtist_shouldReturnBadRequest() {
            val artist = createArtist()
            val original = createSong(artist = artist, title = "Dummy")
            val conflicting = createSong(artist = artist, title = "Angel")

            val result =
                putJsonAuthorized(
                    "/songs/${original.id}",
                    mapOf(
                        "title" to conflicting.title,
                        "releasedAt" to "1998-04",
                        "_links" to
                            mapOf(
                                "artists" to
                                    listOf(
                                        mapOf("href" to "http://localhost:8080/artists/${artist.id}"),
                                    ),
                            ),
                    ),
                    loginAsBootstrapAdmin(),
                )

            status().isBadRequest().match(result)
            assertThat(objectMapper.readTree(result.response.contentAsByteArray)["message"].asText()).isEqualTo(
                "Song already exists for artist: Angel",
            )
            assertThat(songRepository.findById(original.id).orElseThrow().title).isEqualTo("Dummy")
        }
    }
}
