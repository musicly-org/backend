package ly.music.catalog.interfaces.rest.track

import ly.music.catalog.BackendControllerE2eTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

class TrackForReleaseControllerE2eTest : BackendControllerE2eTestSupport() {
    @Nested
    inner class GetReleaseTracks {
        @Test
        fun existingReleaseTracks_shouldReturnOk() {
            val artist = createArtist()
            val album = createAlbum(artist = artist)
            val release = createRelease(album = album)
            val song = createSong(artist = artist)
            val track = createTrack(release = release, song = song)
            val social = createTrackSocial(track = track, spotifyId = "spotify-track-123")

            val body = getJson("/releases/${release.id}/tracks")
            val item = embeddedItems(body).first()

            assertThat(body["page"]["totalElements"].asInt()).isEqualTo(1)
            assertNoId(item)
            assertThat(item["discNumber"].asInt()).isEqualTo(track.discNumber)
            assertThat(item["trackNumber"].asInt()).isEqualTo(track.trackNumber)
            assertThat(item["title"].asText()).isEqualTo(track.title)
            assertThat(item["imageUrl"].asText()).isEqualTo(release.imageUrl)
            assertThat(item["social"]["spotify"].asText()).isEqualTo(social.spotifyId)
            assertThat(link(item, "self")).endsWith("/tracks/${track.id}")
            assertThat(link(item, "release")).endsWith("/releases/${release.id}")
            assertThat(link(item, "song")).endsWith("/songs/${song.id}")
        }

        @Test
        fun missingRelease_shouldReturnNotFound() {
            val missingReleaseId = UUID.randomUUID()

            val result = getResponse("/releases/$missingReleaseId/tracks")

            status().isNotFound().match(result)
            assertThat(objectMapper.readTree(result.response.contentAsByteArray)["message"].asText()).isEqualTo("Release not found: $missingReleaseId")
        }
    }
}
