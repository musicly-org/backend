package ly.music.catalog.interfaces.rest.album

import ly.music.catalog.BackendControllerE2eTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class AlbumControllerE2eTest : BackendControllerE2eTestSupport() {
    @Nested
    inner class GetAlbum {
        @Test
        fun returnsAlbum() {
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
}
