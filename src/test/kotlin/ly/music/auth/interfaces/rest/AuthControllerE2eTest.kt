package ly.music.auth.interfaces.rest

import ly.music.catalog.BackendControllerE2eTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AuthControllerE2eTest : BackendControllerE2eTestSupport() {
    @Nested
    inner class Login {
        @Test
        fun returnsJwtWithRolesAndPermissions() {
            bootstrapAdminProvisioner.ensurePresent()

            val result =
                postJson(
                    "/auth/login",
                    mapOf(
                        "email" to "admin@musicly.local",
                        "password" to "change-this-admin-password",
                    ),
                )

            status().isOk().match(result)

            val body = objectMapper.readTree(result.response.contentAsByteArray)
            assertThat(body["tokenType"].asText()).isEqualTo("Bearer")
            assertThat(body["accessToken"].asText()).isNotBlank
            assertThat(body["roles"].map { it.asText() }).containsExactly("SUPER_ADMIN")
            assertThat(body["permissions"].map { it.asText() })
                .containsExactly("admin:manage", "catalog:read", "catalog:write")
            assertThat(body["expiresInSeconds"].asLong()).isPositive()
        }

        @Test
        fun rejectsInvalidPassword() {
            bootstrapAdminProvisioner.ensurePresent()

            val result =
                postJson(
                    "/auth/login",
                    mapOf(
                        "email" to "admin@musicly.local",
                        "password" to "wrong-password",
                    ),
                )

            status().isUnauthorized().match(result)

            val body = objectMapper.readTree(result.response.contentAsByteArray)
            assertThat(body["message"].asText()).isEqualTo("Invalid email or password")
        }
    }
}
