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
            val artist = createArtist()
            val album = createAlbum(artist = artist)

            val body = getJson("/albums/${album.id}")

            assertNoId(body)
            assertThat(body["title"].asText()).isEqualTo(album.title)
            assertThat(body["releasedAt"].asText()).isEqualTo(album.releasedAt?.value)
            assertThat(body["imageUrl"].asText()).isEqualTo(album.imageUrl)
            assertThat(link(body, "self")).endsWith("/albums/${album.id}")
            assertThat(link(body, "artist")).endsWith("/artists/${artist.id}")
            assertThat(link(body, "album-versions")).endsWith("/albums/${album.id}/versions")
        }
    }
}
