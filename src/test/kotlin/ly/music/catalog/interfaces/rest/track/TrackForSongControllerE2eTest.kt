package ly.music.catalog.interfaces.rest.track

import ly.music.catalog.BackendControllerE2eTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

class TrackForSongControllerE2eTest : BackendControllerE2eTestSupport() {
    @Nested
    inner class GetSongTracks {
        @Test
        fun existingSongTracks_shouldReturnOk() {
            val artist = createArtist()
            val album = createAlbum(artist = artist)
            val release = createRelease(album = album)
            val song = createSong(artist = artist)
            val track = createTrack(release = release, song = song)
            val social = createTrackSocial(track = track, spotifyId = "spotify-track-123")

            val body = getJson("/songs/${song.id}/tracks")
            val item = embeddedItems(body).first()

            assertThat(body["page"]["totalElements"].asInt()).isEqualTo(1)
            assertNoId(item)
            assertThat(item["title"].asText()).isEqualTo(track.title)
            assertThat(item["imageUrl"].asText()).isEqualTo(release.imageUrl)
            assertThat(item["durationSeconds"].asInt()).isEqualTo(track.durationSeconds)
            assertThat(item["releasedAt"].asText()).isEqualTo(track.releasedAt?.value)
            assertThat(item["social"]["spotify"].asText()).isEqualTo(social.spotifyId)
            assertThat(link(item, "self")).endsWith("/tracks/${track.id}")
            assertThat(link(item, "song")).endsWith("/songs/${song.id}")
            assertThat(link(item, "release")).endsWith("/releases/${release.id}")
        }

        @Test
        fun missingSong_shouldReturnNotFound() {
            val missingSongId = UUID.randomUUID()

            val result = getResponse("/songs/$missingSongId/tracks")

            status().isNotFound().match(result)
            assertThat(objectMapper.readTree(result.response.contentAsByteArray)["message"].asText()).isEqualTo("Song not found: $missingSongId")
        }
    }
}
