package ly.music.catalog.interfaces.rest.root

import ly.music.catalog.BackendControllerE2eTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ApiRootControllerE2eTest : BackendControllerE2eTestSupport() {
    @Test
    fun returnsEntryPoints() {
        val body = getJson("/")

        assertThat(link(body, "self")).endsWith("/")
        assertThat(link(body, "artists")).isEqualTo("http://localhost:8080/artists")
        assertThat(link(body, "artist")).isEqualTo("http://localhost:8080/artists/{id}")
        assertThat(link(body, "album")).isEqualTo("http://localhost:8080/albums/{id}")
        assertThat(link(body, "album-version")).isEqualTo("http://localhost:8080/album-versions/{id}")
        assertThat(link(body, "track")).isEqualTo("http://localhost:8080/tracks/{id}")
        assertThat(link(body, "song")).isEqualTo("http://localhost:8080/songs/{id}")
        assertThat(link(body, "song-version")).isEqualTo("http://localhost:8080/song-versions/{id}")
    }
}
