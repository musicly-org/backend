package ly.music

import ly.music.catalog.BackendControllerE2eTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ApiRootControllerE2eTest : BackendControllerE2eTestSupport() {
    @Test
    fun returnsTopLevelEntryPoints() {
        val body = getJson("/")

        assertThat(link(body, "self")).endsWith("/")
        assertThat(link(body, "catalog")).isEqualTo("http://localhost:8080/catalog")
        assertThat(link(body, "auth")).isEqualTo("http://localhost:8080/auth")
    }
}
