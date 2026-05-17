package ly.music.auth.interfaces.rest

import ly.music.catalog.BackendControllerE2eTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AuthRootControllerE2eTest : BackendControllerE2eTestSupport() {
    @Test
    fun authEntryPoints_shouldReturnOk() {
        val body = getJson("/auth")

        assertThat(link(body, "self")).isEqualTo("http://localhost:8080/auth")
        assertThat(link(body, "login")).isEqualTo("http://localhost:8080/auth/login")
        assertThat(link(body, "register")).isEqualTo("http://localhost:8080/auth/register")
    }
}
