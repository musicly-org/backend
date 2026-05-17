package ly.music.auth.interfaces.rest

import ly.music.auth.application.AuthService
import ly.music.auth.application.RegisterUserCommand
import ly.music.auth.configuration.security.IssuedToken
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Clock
import java.time.Duration

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
        return ResponseEntity.ok(token.toResponse(clock))
    }

    @PostMapping("/register")
    fun register(
        @RequestBody request: RegisterRequest,
    ): ResponseEntity<AuthTokenResponse> {
        val token =
            authService.register(
                RegisterUserCommand(
                    email = request.email,
                    password = request.password,
                    displayName = request.displayName,
                ),
            )
        return ResponseEntity.status(HttpStatus.CREATED).body(token.toResponse(clock))
    }

    private fun IssuedToken.toResponse(clock: Clock): AuthTokenResponse =
        AuthTokenResponse(
            accessToken = accessToken,
            tokenType = "Bearer",
            expiresInSeconds = Duration.between(clock.instant(), expiresAt).seconds,
            roles = roles,
            permissions = permissions,
        )
}
