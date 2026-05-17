package ly.music.auth.application

import ly.music.auth.configuration.security.IssuedToken
import ly.music.auth.configuration.security.JwtTokenService
import ly.music.auth.domain.UserEntity
import ly.music.auth.domain.UserRepository
import ly.music.auth.domain.UserRole
import org.springframework.dao.DataIntegrityViolationException
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
            userRepository.findByEmail(normalizeEmail(email))
                ?: throw InvalidCredentialsException()

        if (!user.enabled || !passwordEncoder.matches(password, user.passwordHash)) {
            throw InvalidCredentialsException()
        }

        return jwtTokenService.issueAccessToken(user)
    }

    fun register(command: RegisterUserCommand): IssuedToken {
        val email = normalizeEmail(command.email)
        if (userRepository.existsByEmail(email)) {
            throw EmailAlreadyRegisteredException()
        }

        val user =
            UserEntity(
                email = email,
                passwordHash = requireNotNull(passwordEncoder.encode(command.password)),
                displayName = command.displayName?.trim()?.takeIf { it.isNotEmpty() },
                enabled = true,
            ).also { it.assignRole(UserRole.REGULAR_USER) }

        val savedUser =
            try {
                userRepository.saveAndFlush(user)
            } catch (_: DataIntegrityViolationException) {
                throw EmailAlreadyRegisteredException()
            }

        return jwtTokenService.issueAccessToken(savedUser)
    }

    private fun normalizeEmail(email: String): String = email.trim().lowercase()
}

class InvalidCredentialsException : RuntimeException("Invalid email or password")

class EmailAlreadyRegisteredException : RuntimeException("Email is already registered")
