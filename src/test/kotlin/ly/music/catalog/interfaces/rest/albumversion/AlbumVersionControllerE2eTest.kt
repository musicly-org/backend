package ly.music.catalog.interfaces.rest.albumversion

import ly.music.catalog.BackendControllerE2eTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class AlbumVersionControllerE2eTest : BackendControllerE2eTestSupport() {
    @Nested
    inner class GetAlbumVersion {
        @Test
        fun returnsAlbumVersion() {
            val artist = createArtist()
            val album = createAlbum(artist = artist)
            val albumVersion = createAlbumVersion(album = album, isDefault = true)

            val body = getJson("/album-versions/${albumVersion.id}")

            assertNoId(body)
            assertThat(body["title"].asText()).isEqualTo(albumVersion.title)
            assertThat(body["releasedAt"].asText()).isEqualTo(albumVersion.releasedAt?.value)
            assertThat(body["imageUrl"].asText()).isEqualTo(albumVersion.imageUrl)
            assertThat(link(body, "self")).endsWith("/album-versions/${albumVersion.id}")
            assertThat(link(body, "album")).endsWith("/albums/${album.id}")
            assertThat(link(body, "tracks")).endsWith("/album-versions/${albumVersion.id}/tracks")
        }
    }
}
