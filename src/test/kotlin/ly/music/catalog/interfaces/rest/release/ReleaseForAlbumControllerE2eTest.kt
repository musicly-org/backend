package ly.music.catalog.interfaces.rest.release

import ly.music.catalog.BackendControllerE2eTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ReleaseForAlbumControllerE2eTest : BackendControllerE2eTestSupport() {
    @Nested
    inner class GetReleases {
        @Test
        fun returnsPagedReleases() {
            val artist = createArtist()
            val album = createAlbum(artist = artist)
            val defaultRelease = createRelease(album = album, isDefault = true)
            val nonDefaultRelease = createRelease(album = album, title = "Deluxe Edition")

            val body = getJson("/albums/${album.id}/releases")
            val items = embeddedItems(body)

            assertThat(body["page"]["totalElements"].asInt()).isEqualTo(2)
            assertThat(items).hasSize(2)
            items.forEach(::assertNoId)
            assertThat(items.map { it["title"].asText() })
                .containsExactlyInAnyOrder(defaultRelease.title, nonDefaultRelease.title)
            assertThat(items.map { it["releasedAt"].asText() })
                .containsExactlyInAnyOrder(defaultRelease.releasedAt?.value ?: "", nonDefaultRelease.releasedAt?.value ?: "")
            assertThat(items.map { it["imageUrl"].asText() })
                .containsExactlyInAnyOrder(defaultRelease.imageUrl ?: "", nonDefaultRelease.imageUrl ?: "")
            assertThat(items.map { link(it, "self") })
                .anySatisfy { assertThat(it).endsWith("/releases/${defaultRelease.id}") }
                .anySatisfy { assertThat(it).endsWith("/releases/${nonDefaultRelease.id}") }
            assertThat(items.map { link(it, "album") }).allSatisfy {
                assertThat(it).endsWith("/albums/${album.id}")
            }
            assertThat(items.map { link(it, "tracks") })
                .anySatisfy { assertThat(it).endsWith("/releases/${defaultRelease.id}/tracks") }
                .anySatisfy { assertThat(it).endsWith("/releases/${nonDefaultRelease.id}/tracks") }
        }
    }
}
