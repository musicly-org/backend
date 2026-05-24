package ly.music.catalog.interfaces.rest.release

import ly.music.catalog.BackendControllerE2eTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

class ReleaseControllerE2eTest : BackendControllerE2eTestSupport() {
    @Nested
    inner class GetRelease {
        @Test
        fun existingRelease_shouldReturnOk() {
            val artist = createArtist()
            val album = createAlbum(artist = artist)
            val release = createRelease(album = album, isDefault = true)

            val body = getJson("/releases/${release.id}")

            assertNoId(body)
            assertThat(body["title"].asText()).isEqualTo(release.title)
            assertThat(body["releasedAt"].asText()).isEqualTo(release.releasedAt?.value)
            assertThat(body["imageUrl"].asText()).isEqualTo(release.imageUrl)
            assertThat(body["isDefault"].asBoolean()).isTrue()
            assertThat(link(body, "self")).endsWith("/releases/${release.id}")
            assertThat(link(body, "album")).endsWith("/albums/${album.id}")
            assertThat(link(body, "tracks")).endsWith("/releases/${release.id}/tracks")
        }
    }

    @Nested
    inner class CreateRelease {
        @Test
        fun missingAlbumLink_shouldReturnBadRequest() {
            val result =
                postJsonAuthorized(
                    "/releases",
                    mapOf(
                        "title" to "Mezzanine",
                        "_links" to emptyMap<String, Any>(),
                    ),
                    loginAsBootstrapAdmin(),
                )

            status().isBadRequest().match(result)
            assertThat(objectMapper.readTree(result.response.contentAsByteArray)["message"].asText()).isEqualTo("Missing _links.album")
        }

        @Test
        fun firstAlbumRelease_shouldReturnCreated() {
            val token = loginAsBootstrapAdmin()
            val artist = createArtist()
            val album = createAlbum(artist = artist)

            val result =
                postJsonAuthorized(
                    "/releases",
                    mapOf(
                        "title" to "Original",
                        "releasedAt" to "1998-04-20",
                        "imageUrl" to "https://example.test/original.jpg",
                        "_links" to mapOf("album" to mapOf("href" to "/albums/${album.id}")),
                    ),
                    token,
                )

            status().isCreated().match(result)
            val location = requireNotNull(result.response.getHeader("Location"))
            val releaseId = UUID.fromString(location.substringAfterLast("/"))
            val body = objectMapper.readTree(result.response.contentAsByteArray)

            assertNoId(body)
            assertThat(body["title"].asText()).isEqualTo("Original")
            assertThat(body["releasedAt"].asText()).isEqualTo("1998-04-20")
            assertThat(body["imageUrl"].asText()).isEqualTo("https://example.test/original.jpg")
            assertThat(body["isDefault"].asBoolean()).isTrue()
            assertThat(link(body, "self")).endsWith("/releases/$releaseId")
            assertThat(link(body, "album")).endsWith("/albums/${album.id}")
            assertThat(link(body, "tracks")).endsWith("/releases/$releaseId/tracks")
            assertThat(releaseRepository.findByIdOrThrow(releaseId).isDefault).isTrue()
        }

        @Test
        fun albumWithExistingDefault_shouldReturnCreated() {
            val token = loginAsBootstrapAdmin()
            val artist = createArtist()
            val album = createAlbum(artist = artist)
            createRelease(album = album, title = album.title, isDefault = true)

            val result =
                postJsonAuthorized(
                    "/releases",
                    mapOf(
                        "title" to "Original",
                        "releasedAt" to "1998-04-20",
                        "imageUrl" to "https://example.test/original.jpg",
                        "_links" to mapOf("album" to mapOf("href" to "/albums/${album.id}")),
                    ),
                    token,
                )

            status().isCreated().match(result)
            val location = requireNotNull(result.response.getHeader("Location"))
            val releaseId = UUID.fromString(location.substringAfterLast("/"))
            val body = objectMapper.readTree(result.response.contentAsByteArray)

            assertNoId(body)
            assertThat(body["title"].asText()).isEqualTo("Original")
            assertThat(body["isDefault"].asBoolean()).isFalse()
            assertThat(link(body, "self")).endsWith("/releases/$releaseId")
            assertThat(releaseRepository.findByIdOrThrow(releaseId).isDefault).isFalse()
        }

        @Test
        fun duplicateTitleForSameAlbum_shouldReturnCreated() {
            val token = loginAsBootstrapAdmin()
            val artist = createArtist()
            val album = createAlbum(artist = artist)
            createRelease(album = album, title = "Original", isDefault = true)

            val result =
                postJsonAuthorized(
                    "/releases",
                    mapOf(
                        "title" to " original ",
                        "_links" to mapOf("album" to mapOf("href" to "/albums/${album.id}")),
                    ),
                    token,
                )

            status().isCreated().match(result)
            val location = requireNotNull(result.response.getHeader("Location"))
            val releaseId = UUID.fromString(location.substringAfterLast("/"))
            val release = releaseRepository.findByIdOrThrow(releaseId)

            assertThat(release.title).isEqualTo("original")
            assertThat(release.album.id).isEqualTo(album.id)
            assertThat(release.isDefault).isFalse()
        }

        @Test
        fun duplicateSpotifyId_shouldReturnBadRequest() {
            val token = loginAsBootstrapAdmin()
            val artist = createArtist()
            val album = createAlbum(artist = artist)
            val existingRelease = createRelease(album = album, title = "Original")
            createReleaseSocial(release = existingRelease, spotifyId = "spotify-release-123")

            val result =
                postJsonAuthorized(
                    "/releases",
                    mapOf(
                        "title" to "Deluxe Edition",
                        "spotifyId" to "spotify-release-123",
                        "_links" to mapOf("album" to mapOf("href" to "/albums/${album.id}")),
                    ),
                    token,
                )

            status().isBadRequest().match(result)
            val body = objectMapper.readTree(result.response.contentAsByteArray)
            assertThat(body["message"].asText()).isEqualTo("Spotify release already linked: spotify-release-123")
            assertThat(releaseRepository.findAll()).hasSize(1)
        }

        @Test
        fun updateOnlyFieldIsDefault_shouldReturnBadRequest() {
            val token = loginAsBootstrapAdmin()
            val artist = createArtist()
            val album = createAlbum(artist = artist)

            val result =
                postJsonAuthorized(
                    "/releases",
                    mapOf(
                        "title" to "Original",
                        "isDefault" to true,
                        "_links" to mapOf("album" to mapOf("href" to "/albums/${album.id}")),
                    ),
                    token,
                )

            status().isBadRequest().match(result)
            assertThat(releaseRepository.findAll()).isEmpty()
        }
    }

    @Nested
    inner class UpdateRelease {
        @Test
        fun promotedToDefault_shouldReturnOk() {
            val token = loginAsBootstrapAdmin()
            val artist = createArtist()
            val album = createAlbum(artist = artist)
            val defaultRelease = createRelease(album = album, title = "Original", isDefault = true)
            val deluxeRelease = createRelease(album = album, title = "Deluxe Edition")

            val result =
                putJsonAuthorized(
                    "/releases/${deluxeRelease.id}",
                    mapOf(
                        "title" to deluxeRelease.title,
                        "releasedAt" to deluxeRelease.releasedAt?.value,
                        "imageUrl" to deluxeRelease.imageUrl,
                        "isDefault" to true,
                        "_links" to mapOf("album" to mapOf("href" to "/albums/${album.id}")),
                    ),
                    token,
                )

            status().isOk().match(result)
            val body = objectMapper.readTree(result.response.contentAsByteArray)

            assertThat(body["isDefault"].asBoolean()).isTrue()
            assertThat(releaseRepository.findByIdOrThrow(defaultRelease.id).isDefault).isFalse()
            assertThat(releaseRepository.findByIdOrThrow(deluxeRelease.id).isDefault).isTrue()
        }

        @Test
        fun conflictingSiblingTitle_shouldReturnOk() {
            val token = loginAsBootstrapAdmin()
            val artist = createArtist()
            val album = createAlbum(artist = artist)
            val originalRelease = createRelease(album = album, title = "Original", isDefault = true)
            val deluxeRelease = createRelease(album = album, title = "Deluxe Edition")

            val result =
                putJsonAuthorized(
                    "/releases/${deluxeRelease.id}",
                    mapOf(
                        "title" to " original ",
                        "releasedAt" to deluxeRelease.releasedAt?.value,
                        "imageUrl" to deluxeRelease.imageUrl,
                        "isDefault" to false,
                        "_links" to mapOf("album" to mapOf("href" to "/albums/${album.id}")),
                    ),
                    token,
                )

            status().isOk().match(result)
            val body = objectMapper.readTree(result.response.contentAsByteArray)
            assertThat(body["title"].asText()).isEqualTo("original")
            assertThat(releaseRepository.findByIdOrThrow(originalRelease.id).title).isEqualTo("Original")
            assertThat(releaseRepository.findByIdOrThrow(deluxeRelease.id).title).isEqualTo("original")
        }

        @Test
        fun changingAlbum_shouldReturnOk() {
            val token = loginAsBootstrapAdmin()
            val artist = createArtist()
            val album = createAlbum(artist = artist)
            val anotherAlbum = createAlbum(artist = artist, title = "100th Window")
            val release = createRelease(album = album, title = "Original")

            val result =
                putJsonAuthorized(
                    "/releases/${release.id}",
                    mapOf(
                        "title" to release.title,
                        "releasedAt" to release.releasedAt?.value,
                        "imageUrl" to release.imageUrl,
                        "isDefault" to false,
                        "_links" to mapOf("album" to mapOf("href" to "/albums/${anotherAlbum.id}")),
                    ),
                    token,
                )

            status().isOk().match(result)
            val body = objectMapper.readTree(result.response.contentAsByteArray)
            assertThat(link(body, "album")).endsWith("/albums/${anotherAlbum.id}")
            assertThat(releaseRepository.findByIdOrThrow(release.id).album.id).isEqualTo(anotherAlbum.id)
            assertThat(albumRepository.findById(album.id)).isEmpty
        }

        @Test
        fun movingDefaultReleaseToAnotherAlbum_shouldPromoteNewDefaultForOldAlbum() {
            val token = loginAsBootstrapAdmin()
            val artist = createArtist()
            val album = createAlbum(artist = artist)
            val anotherAlbum = createAlbum(artist = artist, title = "100th Window")
            val defaultRelease = createRelease(album = album, title = "Original", releasedAt = "1998-04-20", isDefault = true)
            val remainingRelease = createRelease(album = album, title = "Deluxe Edition", releasedAt = "1998-05-01")

            val result =
                putJsonAuthorized(
                    "/releases/${defaultRelease.id}",
                    mapOf(
                        "title" to defaultRelease.title,
                        "releasedAt" to defaultRelease.releasedAt?.value,
                        "imageUrl" to defaultRelease.imageUrl,
                        "isDefault" to false,
                        "_links" to mapOf("album" to mapOf("href" to "/albums/${anotherAlbum.id}")),
                    ),
                    token,
                )

            status().isOk().match(result)
            assertThat(releaseRepository.findByIdOrThrow(defaultRelease.id).album.id).isEqualTo(anotherAlbum.id)
            assertThat(releaseRepository.findByIdOrThrow(defaultRelease.id).isDefault).isTrue()
            assertThat(releaseRepository.findByIdOrThrow(remainingRelease.id).isDefault).isTrue()
        }
    }

    @Nested
    inner class PatchRelease {
        @Test
        fun titleOnly_shouldUpdateOnlyProvidedField() {
            val token = loginAsBootstrapAdmin()
            val artist = createArtist()
            val album = createAlbum(artist = artist)
            val release = createRelease(album = album, title = "Original", releasedAt = "1998-04-20", imageUrl = "https://example.test/original.jpg", isDefault = true)

            val result =
                patchJsonAuthorized(
                    "/releases/${release.id}",
                    mapOf("title" to " Deluxe Edition "),
                    token,
                )

            status().isOk().match(result)
            val body = objectMapper.readTree(result.response.contentAsByteArray)
            val patchedRelease = releaseRepository.findByIdOrThrow(release.id)

            assertThat(body["title"].asText()).isEqualTo("Deluxe Edition")
            assertThat(body["releasedAt"].asText()).isEqualTo("1998-04-20")
            assertThat(body["imageUrl"].asText()).isEqualTo("https://example.test/original.jpg")
            assertThat(body["isDefault"].asBoolean()).isTrue()
            assertThat(patchedRelease.title).isEqualTo("Deluxe Edition")
            assertThat(patchedRelease.releasedAt?.value).isEqualTo("1998-04-20")
            assertThat(patchedRelease.imageUrl).isEqualTo("https://example.test/original.jpg")
        }

        @Test
        fun emptyBody_shouldNotUpdateAnything() {
            val token = loginAsBootstrapAdmin()
            val artist = createArtist()
            val album = createAlbum(artist = artist)
            val release = createRelease(album = album, title = "Original", releasedAt = "1998-04-20", imageUrl = "https://example.test/original.jpg", isDefault = true)

            val result =
                patchJsonAuthorized(
                    "/releases/${release.id}",
                    emptyMap<String, Any>(),
                    token,
                )

            status().isOk().match(result)
            val patchedRelease = releaseRepository.findByIdOrThrow(release.id)
            assertThat(patchedRelease.title).isEqualTo("Original")
            assertThat(patchedRelease.releasedAt?.value).isEqualTo("1998-04-20")
            assertThat(patchedRelease.imageUrl).isEqualTo("https://example.test/original.jpg")
            assertThat(patchedRelease.isDefault).isTrue()
        }

        @Test
        fun albumOnly_shouldMoveReleaseAndDeleteEmptyOldAlbum() {
            val token = loginAsBootstrapAdmin()
            val artist = createArtist()
            val album = createAlbum(artist = artist)
            val anotherAlbum = createAlbum(artist = artist, title = "100th Window")
            val release = createRelease(album = album, title = "Original", isDefault = true)

            val result =
                patchJsonAuthorized(
                    "/releases/${release.id}",
                    mapOf(
                        "_links" to mapOf("album" to mapOf("href" to "/albums/${anotherAlbum.id}")),
                    ),
                    token,
                )

            status().isOk().match(result)
            val body = objectMapper.readTree(result.response.contentAsByteArray)
            assertThat(link(body, "album")).endsWith("/albums/${anotherAlbum.id}")
            assertThat(releaseRepository.findByIdOrThrow(release.id).album.id).isEqualTo(anotherAlbum.id)
            assertThat(albumRepository.findById(album.id)).isEmpty
        }

        @Test
        fun spotifyLinkedRelease_shouldMoveAlbumWithoutDeletingRelease() {
            val token = loginAsBootstrapAdmin()
            val artist = createArtist()
            val album = createAlbum(artist = artist)
            val anotherAlbum = createAlbum(artist = artist, title = "100th Window")
            val release = createRelease(album = album, title = "Original", isDefault = true)
            createReleaseSocial(release = release, spotifyId = "spotify-release-123")

            val result =
                patchJsonAuthorized(
                    "/releases/${release.id}",
                    mapOf(
                        "_links" to mapOf("album" to mapOf("href" to "/albums/${anotherAlbum.id}")),
                    ),
                    token,
                )

            status().isOk().match(result)
            assertThat(releaseRepository.findByIdOrThrow(release.id).album.id).isEqualTo(anotherAlbum.id)
            assertThat(releaseSocialRepository.findBySpotifyId("spotify-release-123")).isNotNull()
            assertThat(albumRepository.findById(album.id)).isEmpty
        }
    }

    @Nested
    inner class DeleteRelease {
        @Test
        fun deleteReleaseWithTracks_shouldReturnBadRequest() {
            val token = loginAsBootstrapAdmin()
            val artist = createArtist()
            val album = createAlbum(artist = artist)
            val originalRelease = createRelease(album = album, title = "Original", releasedAt = "1998-04-20", isDefault = true)
            val releaseWithTracks = createRelease(album = album, title = "Deluxe Edition", releasedAt = "1998-05-01")
            val song = createSong(artist = artist)
            createTrack(release = releaseWithTracks, song = song)
            createReleaseSocial(release = releaseWithTracks, spotifyId = "spotify-release-linked")

            val result = deleteAuthorized("/releases/${releaseWithTracks.id}", token)

            status().isBadRequest().match(result)
            val body = objectMapper.readTree(result.response.contentAsByteArray)
            assertThat(body["message"].asText()).isEqualTo("Cannot delete a release that still has tracks")
            assertThat(releaseRepository.findByIdOrThrow(releaseWithTracks.id).isDefault).isFalse()
            assertThat(releaseSocialRepository.findBySpotifyId("spotify-release-linked")).isNotNull()
            assertThat(trackRepository.findByReleaseId(releaseWithTracks.id, org.springframework.data.domain.Pageable.unpaged()).totalElements).isEqualTo(1)
            assertThat(releaseRepository.findByIdOrThrow(originalRelease.id).isDefault).isTrue()
        }

        @Test
        fun deleteCurrentDefaultWithAlternativeRelease_shouldReturnNoContent() {
            val token = loginAsBootstrapAdmin()
            val artist = createArtist()
            val album = createAlbum(artist = artist)
            val originalRelease =
                createRelease(album = album, title = "Original", releasedAt = "1998-04-20", isDefault = true)
            val deluxeRelease = createRelease(album = album, title = "Deluxe Edition", releasedAt = "1998-05-01")

            val result = deleteAuthorized("/releases/${originalRelease.id}", token)

            status().isNoContent().match(result)
            assertThat(releaseRepository.findById(originalRelease.id)).isEmpty
            assertThat(releaseRepository.findByIdOrThrow(deluxeRelease.id).isDefault).isTrue()

            val body = getJson("/releases/${deluxeRelease.id}")
            assertThat(body["isDefault"].asBoolean()).isTrue()
        }

        @Test
        fun deleteSpotifyLinkedRelease_shouldReturnNoContent() {
            val token = loginAsBootstrapAdmin()
            val artist = createArtist()
            val album = createAlbum(artist = artist)
            val originalRelease = createRelease(album = album, title = "Original", releasedAt = "1998-04-20", isDefault = true)
            val spotifyLinkedRelease = createRelease(album = album, title = "Deluxe Edition", releasedAt = "1998-05-01")
            createReleaseSocial(release = spotifyLinkedRelease, spotifyId = "spotify-release-linked")

            val result = deleteAuthorized("/releases/${spotifyLinkedRelease.id}", token)

            status().isNoContent().match(result)
            assertThat(releaseRepository.findById(spotifyLinkedRelease.id)).isEmpty
            assertThat(releaseRepository.findByIdOrThrow(originalRelease.id).isDefault).isTrue()
        }

        @Test
        fun deleteOnlyAlbumRelease_shouldReturnBadRequest() {
            val token = loginAsBootstrapAdmin()
            val artist = createArtist()
            val album = createAlbum(artist = artist)
            val onlyRelease = createRelease(album = album, title = "Original", isDefault = true)

            val result = deleteAuthorized("/releases/${onlyRelease.id}", token)

            status().isBadRequest().match(result)
            val body = objectMapper.readTree(result.response.contentAsByteArray)
            assertThat(body["message"].asText()).isEqualTo("Cannot delete an only release for album")
            assertThat(releaseRepository.findByIdOrThrow(onlyRelease.id).isDefault).isTrue()

            val releaseBody = getJson("/releases/${onlyRelease.id}")
            assertThat(releaseBody["isDefault"].asBoolean()).isTrue()
        }
    }
}
