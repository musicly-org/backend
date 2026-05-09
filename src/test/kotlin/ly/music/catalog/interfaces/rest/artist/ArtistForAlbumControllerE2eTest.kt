package ly.music.catalog.interfaces.rest.artist

import ly.music.catalog.BackendControllerE2eTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ArtistForAlbumControllerE2eTest : BackendControllerE2eTestSupport() {
    @Nested
    inner class GetAlbumArtists {
        @Test
        fun returnsPagedArtists() {
            val primaryArtist = createArtist()
            val collaborator = createArtist(name = "Madonna")
            val album = createAlbum(artist = primaryArtist, artists = listOf(primaryArtist, collaborator))

            val body = getJson("/albums/${album.id}/artists")
            val items = embeddedItems(body)

            assertThat(body["page"]["totalElements"].asInt()).isEqualTo(2)
            assertThat(items.map { it["name"].asText() }).containsExactlyInAnyOrder(primaryArtist.name, collaborator.name)
            assertThat(items.map { link(it, "self") })
                .anySatisfy { assertThat(it).endsWith("/artists/${primaryArtist.id}") }
                .anySatisfy { assertThat(it).endsWith("/artists/${collaborator.id}") }
        }
    }
}
