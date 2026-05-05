package ly.music.catalog.interfaces.rest.albumversion

import ly.music.catalog.BackendControllerE2eTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class AlbumVersionForAlbumControllerE2eTest : BackendControllerE2eTestSupport() {
    @Nested
    inner class GetAlbumVersions {
        @Test
        fun returnsPagedAlbumVersions() {
            val artist = createArtist()
            val album = createAlbum(artist = artist)
            val defaultAlbumVersion = createAlbumVersion(album = album, isDefault = true)
            val nonDefaultAlbumVersion = createAlbumVersion(album = album, title = "Deluxe Edition")

            val body = getJson("/albums/${album.id}/versions")
            val items = embeddedItems(body)

            assertThat(body["page"]["totalElements"].asInt()).isEqualTo(2)
            assertThat(items).hasSize(2)
            items.forEach(::assertNoId)
            assertThat(items.map { it["title"].asText() })
                .containsExactlyInAnyOrder(defaultAlbumVersion.title, nonDefaultAlbumVersion.title)
            assertThat(items.map { it["releasedAt"].asText() })
                .containsExactlyInAnyOrder(defaultAlbumVersion.releasedAt?.value ?: "", nonDefaultAlbumVersion.releasedAt?.value ?: "")
            assertThat(items.map { it["imageUrl"].asText() })
                .containsExactlyInAnyOrder(defaultAlbumVersion.imageUrl ?: "", nonDefaultAlbumVersion.imageUrl ?: "")
            assertThat(items.map { link(it, "self") })
                .anySatisfy { assertThat(it).endsWith("/album-versions/${defaultAlbumVersion.id}") }
                .anySatisfy { assertThat(it).endsWith("/album-versions/${nonDefaultAlbumVersion.id}") }
            assertThat(items.map { link(it, "album") }).allSatisfy {
                assertThat(it).endsWith("/albums/${album.id}")
            }
            assertThat(items.map { link(it, "tracks") })
                .anySatisfy { assertThat(it).endsWith("/album-versions/${defaultAlbumVersion.id}/tracks") }
                .anySatisfy { assertThat(it).endsWith("/album-versions/${nonDefaultAlbumVersion.id}/tracks") }
        }
    }
}
