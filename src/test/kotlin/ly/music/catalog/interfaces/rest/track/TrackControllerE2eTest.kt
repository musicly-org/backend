package ly.music.catalog.interfaces.rest.track

import ly.music.catalog.BackendControllerE2eTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

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
}
