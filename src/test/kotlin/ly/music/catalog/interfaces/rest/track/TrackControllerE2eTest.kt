package ly.music.catalog.interfaces.rest.track

import ly.music.catalog.BackendControllerE2eTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class TrackControllerE2eTest : BackendControllerE2eTestSupport() {
    @Nested
    inner class GetTrack {
        @Test
        fun existingTrack_shouldReturnOk() {
            val artist = createArtist()
            val album = createAlbum(artist = artist)
            val release = createRelease(album = album)
            val song = createSong(artist = artist)
            val track = createTrack(release = release, song = song)
            val social = createTrackSocial(track = track, spotifyId = "spotify-track-123")

            val body = getJson("/tracks/${track.id}")

            assertNoId(body)
            assertThat(body["title"].asText()).isEqualTo(track.title)
            assertThat(body["imageUrl"].asText()).isEqualTo(release.imageUrl)
            assertThat(body["durationSeconds"].asInt()).isEqualTo(track.durationSeconds)
            assertThat(body["releasedAt"].asText()).isEqualTo(track.releasedAt?.value)
            assertThat(body["discNumber"].asInt()).isEqualTo(track.discNumber)
            assertThat(body["trackNumber"].asInt()).isEqualTo(track.trackNumber)
            assertThat(body["social"]["spotify"].asText()).isEqualTo(social.spotifyId)
            assertThat(link(body, "self")).endsWith("/tracks/${track.id}")
            assertThat(link(body, "song")).endsWith("/songs/${song.id}")
            assertThat(link(body, "release")).endsWith("/releases/${release.id}")
        }
    }

    @Nested
    inner class CreateTrack {
        @Test
        fun duplicateSpotifyId_shouldReturnBadRequest() {
            val token = loginAsBootstrapAdmin()
            val artist = createArtist()
            val album = createAlbum(artist = artist)
            val release = createRelease(album = album)
            val firstSong = createSong(artist = artist, title = "First Song")
            val secondSong = createSong(artist = artist, title = "Second Song")
            val existingTrack = createTrack(release = release, song = firstSong, title = "Existing", discNumber = 1, trackNumber = 1)
            createTrackSocial(track = existingTrack, spotifyId = "spotify-track-occupied")

            val result =
                postJsonAuthorized(
                    "/tracks",
                    mapOf(
                        "title" to "Second Song",
                        "discNumber" to 1,
                        "trackNumber" to 2,
                        "spotifyId" to "spotify-track-occupied",
                        "_links" to
                            mapOf(
                                "song" to mapOf("href" to "/songs/${secondSong.id}"),
                                "release" to mapOf("href" to "/releases/${release.id}"),
                            ),
                    ),
                    token,
                )

            status().isBadRequest().match(result)
            val body = objectMapper.readTree(result.response.contentAsByteArray)
            assertThat(body["message"].asText()).isEqualTo("Spotify track already linked: spotify-track-occupied")
            assertThat(trackRepository.findAll()).hasSize(1)
            assertThat(trackRepository.findByIdOrThrow(existingTrack.id).trackNumber).isEqualTo(1)
        }

        @Test
        fun nonPositiveDiscNumber_shouldReturnBadRequest() {
            val token = loginAsBootstrapAdmin()
            val artist = createArtist()
            val album = createAlbum(artist = artist)
            val release = createRelease(album = album)
            val song = createSong(artist = artist)

            val result =
                postJsonAuthorized(
                    "/tracks",
                    mapOf(
                        "title" to "Teardrop",
                        "durationSeconds" to 330,
                        "discNumber" to 0,
                        "trackNumber" to 1,
                        "_links" to
                            mapOf(
                                "song" to mapOf("href" to "/songs/${song.id}"),
                                "release" to mapOf("href" to "/releases/${release.id}"),
                            ),
                    ),
                    token,
                )

            status().isBadRequest().match(result)
            assertThat(objectMapper.readTree(result.response.contentAsByteArray)["message"].asText()).isEqualTo(
                "Track discNumber must be greater than 0",
            )
        }

        @Test
        fun nonPositiveDurationSeconds_shouldReturnBadRequest() {
            val token = loginAsBootstrapAdmin()
            val artist = createArtist()
            val album = createAlbum(artist = artist)
            val release = createRelease(album = album)
            val song = createSong(artist = artist)

            val result =
                postJsonAuthorized(
                    "/tracks",
                    mapOf(
                        "title" to "Teardrop",
                        "durationSeconds" to 0,
                        "discNumber" to 1,
                        "trackNumber" to 1,
                        "_links" to
                            mapOf(
                                "song" to mapOf("href" to "/songs/${song.id}"),
                                "release" to mapOf("href" to "/releases/${release.id}"),
                            ),
                    ),
                    token,
                )

            status().isBadRequest().match(result)
            assertThat(objectMapper.readTree(result.response.contentAsByteArray)["message"].asText()).isEqualTo(
                "Track durationSeconds must be greater than 0",
            )
        }

        @Test
        fun nonPositiveTrackNumber_shouldReturnBadRequest() {
            val token = loginAsBootstrapAdmin()
            val artist = createArtist()
            val album = createAlbum(artist = artist)
            val release = createRelease(album = album)
            val song = createSong(artist = artist)

            val result =
                postJsonAuthorized(
                    "/tracks",
                    mapOf(
                        "title" to "Teardrop",
                        "durationSeconds" to 330,
                        "discNumber" to 1,
                        "trackNumber" to 0,
                        "_links" to
                            mapOf(
                                "song" to mapOf("href" to "/songs/${song.id}"),
                                "release" to mapOf("href" to "/releases/${release.id}"),
                            ),
                    ),
                    token,
                )

            status().isBadRequest().match(result)
            assertThat(objectMapper.readTree(result.response.contentAsByteArray)["message"].asText()).isEqualTo(
                "Track trackNumber must be greater than 0",
            )
        }
    }
}
