package ly.music.catalog.interfaces.rest.album

import ly.music.catalog.BackendControllerE2eTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class AlbumForArtistControllerE2eTest : BackendControllerE2eTestSupport() {
    @Nested
    inner class GetArtistAlbums {
        @Test
        fun returnsPagedAlbums() {
            val artist = createArtist()
            val album = createAlbum(artist = artist)
            createAlbumVersion(album = album, isDefault = true)

            val body = getJson("/artists/${artist.id}/albums")
            val item = embeddedItems(body).first()

            assertThat(body["page"]["totalElements"].asInt()).isEqualTo(1)
            assertNoId(item)
            assertThat(item["title"].asText()).isEqualTo(album.title)
            assertThat(item["releasedAt"].asText()).isEqualTo(album.releasedAt?.value)
            assertThat(item["imageUrl"].asText()).isEqualTo(album.imageUrl)
            assertThat(link(item, "self")).endsWith("/albums/${album.id}")
            assertThat(link(item, "artist")).endsWith("/artists/${artist.id}")
            assertThat(link(item, "album-versions")).endsWith("/albums/${album.id}/versions")
        }
    }
}
