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
            val collaborator = createArtist(name = "Madonna")
            val album = createAlbum(artist = artist, artists = listOf(artist, collaborator))
            createRelease(album = album, isDefault = true)

            val body = getJson("/artists/${artist.id}/albums")
            val item = embeddedItems(body).first()

            assertThat(body["page"]["totalElements"].asInt()).isEqualTo(1)
            assertNoId(item)
            assertThat(item["title"].asText()).isEqualTo(album.title)
            assertThat(item["releasedAt"].asText()).isEqualTo(album.releasedAt?.value)
            assertThat(item["imageUrl"].asText()).isEqualTo(album.imageUrl)
            assertThat(link(item, "self")).endsWith("/albums/${album.id}")
            assertThat(link(item, "artists")).endsWith("/albums/${album.id}/artists")
            assertThat(link(item, "releases")).endsWith("/albums/${album.id}/releases")
        }

        @Test
        fun returnsAlbumsForCollaboratingArtist() {
            val primaryArtist = createArtist()
            val collaborator = createArtist(name = "Madonna")
            val album = createAlbum(artist = primaryArtist, artists = listOf(primaryArtist, collaborator))

            val body = getJson("/artists/${collaborator.id}/albums")
            val item = embeddedItems(body).first()

            assertThat(body["page"]["totalElements"].asInt()).isEqualTo(1)
            assertThat(link(item, "self")).endsWith("/albums/${album.id}")
        }
    }
}
