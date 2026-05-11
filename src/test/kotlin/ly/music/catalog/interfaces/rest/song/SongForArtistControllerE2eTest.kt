package ly.music.catalog.interfaces.rest.song

import ly.music.catalog.BackendControllerE2eTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SongForArtistControllerE2eTest : BackendControllerE2eTestSupport() {
    @Nested
    inner class GetArtistSongs {
        @Test
        fun returnsPagedSongs() {
            val artist = createArtist()
            val collaborator = createArtist(name = "Madonna")
            val song = createSong(artist = artist, artists = listOf(artist, collaborator))

            val body = getJson("/artists/${artist.id}/songs")
            val item = embeddedItems(body).first()

            assertThat(body["page"]["totalElements"].asInt()).isEqualTo(1)
            assertNoId(item)
            assertThat(item["title"].asText()).isEqualTo(song.title)
            assertThat(item["releasedAt"].asText()).isEqualTo(song.releasedAt?.value)
            assertThat(link(item, "self")).endsWith("/songs/${song.id}")
            assertThat(link(item, "artists")).endsWith("/songs/${song.id}/artists")
            assertThat(link(item, "tracks")).endsWith("/songs/${song.id}/tracks")
        }

        @Test
        fun returnsSongsForCollaboratingArtist() {
            val primaryArtist = createArtist()
            val collaborator = createArtist(name = "Madonna")
            val song = createSong(artist = primaryArtist, artists = listOf(primaryArtist, collaborator))

            val body = getJson("/artists/${collaborator.id}/songs")
            val item = embeddedItems(body).first()

            assertThat(body["page"]["totalElements"].asInt()).isEqualTo(1)
            assertThat(link(item, "self")).endsWith("/songs/${song.id}")
        }
    }
}
