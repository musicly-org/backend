package ly.music.auth.interfaces.rest

import ly.music.auth.domain.UserRole
import ly.music.catalog.BackendControllerE2eTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AuthControllerE2eTest : BackendControllerE2eTestSupport() {
    @Nested
    inner class Register {
        @Test
        fun validRequest_shouldCreateRegularUserAndReturnCreated() {
            val result =
                postJson(
                    "/auth/register",
                    mapOf(
                        "email" to "listener@musicly.local",
                        "password" to "change-me",
                        "displayName" to "Listener",
                    ),
                )

            status().isCreated().match(result)

            val body = objectMapper.readTree(result.response.contentAsByteArray)
            assertThat(body["tokenType"].asText()).isEqualTo("Bearer")
            assertThat(body["accessToken"].asText()).isNotBlank
            assertThat(body["roles"].map { it.asText() }).containsExactly("REGULAR_USER")
            assertThat(body["permissions"].map { it.asText() }).containsExactly("catalog:read")
            assertThat(body["expiresInSeconds"].asLong()).isPositive()

            val user =
                userRepository.findByEmail("listener@musicly.local")
                    ?: error("Expected registered user to be persisted")
            assertThat(user.displayName).isEqualTo("Listener")
            assertThat(user.enabled).isTrue()
            assertThat(user.roles).containsExactly(UserRole.REGULAR_USER)
        }

        @Test
        fun duplicateEmail_shouldReturnConflict() {
            postJson(
                "/auth/register",
                mapOf(
                    "email" to "listener@musicly.local",
                    "password" to "change-me",
                ),
            )

            val result =
                postJson(
                    "/auth/register",
                    mapOf(
                        "email" to " listener@musicly.local ",
                        "password" to "change-me-again",
                    ),
                )

            status().isConflict().match(result)

            val body = objectMapper.readTree(result.response.contentAsByteArray)
            assertThat(body["message"].asText()).isEqualTo("Email is already registered")
        }

        @Test
        fun blankEmail_shouldReturnBadRequest() {
            val result =
                postJson(
                    "/auth/register",
                    mapOf(
                        "email" to "   ",
                        "password" to "change-me",
                    ),
                )

            status().isBadRequest().match(result)

            val body = objectMapper.readTree(result.response.contentAsByteArray)
            assertThat(body["message"].asText()).isEqualTo("Email must not be blank")
            assertThat(userRepository.count()).isZero()
        }

        @Test
        fun blankPassword_shouldReturnBadRequest() {
            val result =
                postJson(
                    "/auth/register",
                    mapOf(
                        "email" to "listener@musicly.local",
                        "password" to "",
                    ),
                )

            status().isBadRequest().match(result)

            val body = objectMapper.readTree(result.response.contentAsByteArray)
            assertThat(body["message"].asText()).isEqualTo("Password must not be blank")
            assertThat(userRepository.count()).isZero()
        }

        @Test
        fun emailLongerThanDatabaseLimit_shouldReturnBadRequest() {
            val result =
                postJson(
                    "/auth/register",
                    mapOf(
                        "email" to "${"a".repeat(309)}@example.test",
                        "password" to "change-me",
                    ),
                )

            status().isBadRequest().match(result)

            val body = objectMapper.readTree(result.response.contentAsByteArray)
            assertThat(body["message"].asText()).isEqualTo("Email must not exceed 320 characters")
            assertThat(userRepository.count()).isZero()
        }

        @Test
        fun displayNameLongerThanDatabaseLimit_shouldReturnBadRequest() {
            val result =
                postJson(
                    "/auth/register",
                    mapOf(
                        "email" to "listener@musicly.local",
                        "password" to "change-me",
                        "displayName" to "a".repeat(256),
                    ),
                )

            status().isBadRequest().match(result)

            val body = objectMapper.readTree(result.response.contentAsByteArray)
            assertThat(body["message"].asText()).isEqualTo("Display name must not exceed 255 characters")
            assertThat(userRepository.count()).isZero()
        }
    }

    @Nested
    inner class Login {
        @Test
        fun validCredentials_shouldReturnOk() {
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
        fun invalidPassword_shouldReturnUnauthorized() {
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

        @Test
        fun registeredUser_shouldBeAbleToLogin() {
            postJson(
                "/auth/register",
                mapOf(
                    "email" to "listener@musicly.local",
                    "password" to "change-me",
                    "displayName" to "Listener",
                ),
            )

            val result =
                postJson(
                    "/auth/login",
                    mapOf(
                        "email" to "listener@musicly.local",
                        "password" to "change-me",
                    ),
                )

            status().isOk().match(result)

            val body = objectMapper.readTree(result.response.contentAsByteArray)
            assertThat(body["roles"].map { it.asText() }).containsExactly("REGULAR_USER")
            assertThat(body["permissions"].map { it.asText() }).containsExactly("catalog:read")
        }
    }
}
