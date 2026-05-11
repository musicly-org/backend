package ly.music.catalog.interfaces.rest.release

import ly.music.catalog.BackendControllerE2eTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ReleaseControllerE2eTest : BackendControllerE2eTestSupport() {
    @Nested
    inner class GetRelease {
        @Test
        fun returnsRelease() {
            val artist = createArtist()
            val album = createAlbum(artist = artist)
            val release = createRelease(album = album, isDefault = true)

            val body = getJson("/releases/${release.id}")

            assertNoId(body)
            assertThat(body["title"].asText()).isEqualTo(release.title)
            assertThat(body["releasedAt"].asText()).isEqualTo(release.releasedAt?.value)
            assertThat(body["imageUrl"].asText()).isEqualTo(release.imageUrl)
            assertThat(link(body, "self")).endsWith("/releases/${release.id}")
            assertThat(link(body, "album")).endsWith("/albums/${album.id}")
            assertThat(link(body, "tracks")).endsWith("/releases/${release.id}/tracks")
        }
    }

    @Nested
    inner class CreateRelease {
        @Test
        fun returnsBadRequestWhenAlbumLinkIsMissing() {
            val result =
                postJsonAuthorized(
                    "/releases",
                    mapOf(
                        "title" to "Mezzanine",
                        "_links" to emptyMap<String, Any>(),
                    ),
                    loginAsBootstrapAdmin(),
                )

            status().isBadRequest().match(result)
            assertThat(objectMapper.readTree(result.response.contentAsByteArray)["message"].asText()).isEqualTo("Missing _links.album")
        }
    }
}
