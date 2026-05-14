package ly.music.catalog.interfaces.rest.album

import ly.music.catalog.BackendControllerE2eTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Pageable
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AlbumControllerE2eTest : BackendControllerE2eTestSupport() {
    @Nested
    inner class GetAlbum {
        @Test
        fun existingAlbum_shouldReturnOk() {
            val primaryArtist = createArtist()
            val featuredArtist = createArtist(name = "Madonna")
            val album = createAlbum(artist = primaryArtist, artists = listOf(primaryArtist, featuredArtist))

            val body = getJson("/albums/${album.id}")

            assertNoId(body)
            assertThat(body["title"].asText()).isEqualTo(album.title)
            assertThat(body["releasedAt"].asText()).isEqualTo(album.releasedAt?.value)
            assertThat(body["imageUrl"].asText()).isEqualTo(album.imageUrl)
            assertThat(link(body, "self")).endsWith("/albums/${album.id}")
            assertThat(link(body, "artists")).endsWith("/albums/${album.id}/artists")
            assertThat(link(body, "releases")).endsWith("/albums/${album.id}/releases")
        }
    }

    @Nested
    inner class CreateAlbum {
        @Test
        fun adminRequest_shouldReturnCreatedAndCreateDefaultRelease() {
            val artist = createArtist()

            val result =
                postJsonAuthorized(
                    "/albums",
                    mapOf(
                        "title" to "Mezzanine",
                        "releasedAt" to "1998",
                        "imageUrl" to "https://example.test/album.jpg",
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
            val createdAlbum = albumRepository.findAll().single()
            val createdRelease = releaseRepository.findByAlbumId(createdAlbum.id, Pageable.unpaged()).single()

            assertNoId(body)
            assertThat(body["title"].asText()).isEqualTo("Mezzanine")
            assertThat(result.response.getHeader("Location")).endsWith("/albums/${createdAlbum.id}")
            assertThat(link(body, "self")).endsWith("/albums/${createdAlbum.id}")
            assertThat(link(body, "releases")).endsWith("/albums/${createdAlbum.id}/releases")

            assertThat(createdRelease.album.id).isEqualTo(createdAlbum.id)
            assertThat(createdRelease.title).isEqualTo(createdAlbum.title)
            assertThat(createdRelease.releasedAt?.value).isEqualTo(createdAlbum.releasedAt?.value)
            assertThat(createdRelease.imageUrl).isEqualTo(createdAlbum.imageUrl)
            assertThat(createdRelease.isDefault).isTrue()
        }

        @Test
        fun missingArtistLinks_shouldReturnBadRequest() {
            val result =
                postJsonAuthorized(
                    "/albums",
                    mapOf(
                        "title" to "Mezzanine",
                        "_links" to emptyMap<String, Any>(),
                    ),
                    loginAsBootstrapAdmin(),
                )

            status().isBadRequest().match(result)
            assertThat(objectMapper.readTree(result.response.contentAsByteArray)["message"].asText()).isEqualTo("Missing _links.artists")
        }

        @Test
        fun collaboratorConflict_shouldReturnBadRequest() {
            val primaryArtist = createArtist(name = "Massive Attack")
            val collaborator = createArtist(name = "Tracey Thorn")
            createAlbum(artist = collaborator, title = "Protection")

            val result =
                postJsonAuthorized(
                    "/albums",
                    mapOf(
                        "title" to "Protection",
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
                "Album already exists for artist: Protection",
            )
        }
    }

    @Nested
    inner class UpdateAlbum {
        @Test
        fun conflictingTitleForSameArtist_shouldReturnBadRequest() {
            val artist = createArtist()
            val original = createAlbum(artist = artist, title = "Dummy")
            val conflicting = createAlbum(artist = artist, title = "Protection")

            val result =
                putJsonAuthorized(
                    "/albums/${original.id}",
                    mapOf(
                        "title" to conflicting.title,
                        "releasedAt" to "1994",
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
                "Album already exists for artist: Protection",
            )
            assertThat(albumRepository.findById(original.id).orElseThrow().title).isEqualTo("Dummy")
        }
    }
}
