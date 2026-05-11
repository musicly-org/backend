package ly.music.auth.interfaces.rest

import ly.music.auth.application.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Clock
import java.time.Duration

data class LoginRequest(
    val email: String,
    val password: String,
)

data class AuthTokenResponse(
    val accessToken: String,
    val tokenType: String,
    val expiresInSeconds: Long,
    val roles: List<String>,
    val permissions: List<String>,
)

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
    private val clock: Clock,
) {
    @PostMapping("/login")
    fun login(
        @RequestBody request: LoginRequest,
    ): ResponseEntity<AuthTokenResponse> {
        val token = authService.login(request.email, request.password)
        return ResponseEntity.ok(
            AuthTokenResponse(
                accessToken = token.accessToken,
                tokenType = "Bearer",
                expiresInSeconds = Duration.between(clock.instant(), token.expiresAt).seconds,
                roles = token.roles,
                permissions = token.permissions,
            ),
        )
    }
}
