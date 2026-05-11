package ly.music.auth.application

import ly.music.auth.configuration.security.IssuedToken
import ly.music.auth.configuration.security.JwtTokenService
import ly.music.auth.domain.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenService: JwtTokenService,
) {
    fun login(
        email: String,
        password: String,
    ): IssuedToken {
        val user =
            userRepository.findByEmail(email.trim().lowercase())
                ?: throw InvalidCredentialsException()

        if (!user.enabled || !passwordEncoder.matches(password, user.passwordHash)) {
            throw InvalidCredentialsException()
        }

        return jwtTokenService.issueAccessToken(user)
    }
}

class InvalidCredentialsException : RuntimeException("Invalid email or password")
