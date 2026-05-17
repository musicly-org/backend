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
    companion object {
        private const val MAX_EMAIL_LENGTH = 320
        private const val MAX_DISPLAY_NAME_LENGTH = 255
    }

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
        if (email.isBlank()) {
            throw InvalidRegistrationException("Email must not be blank")
        }

        if (email.length > MAX_EMAIL_LENGTH) {
            throw InvalidRegistrationException("Email must not exceed 320 characters")
        }

        if (command.password.isBlank()) {
            throw InvalidRegistrationException("Password must not be blank")
        }

        val displayName = command.displayName?.trim()?.takeIf { it.isNotEmpty() }
        if (displayName != null && displayName.length > MAX_DISPLAY_NAME_LENGTH) {
            throw InvalidRegistrationException("Display name must not exceed 255 characters")
        }

        if (userRepository.existsByEmail(email)) {
            throw EmailAlreadyRegisteredException()
        }

        val user =
            UserEntity(
                email = email,
                passwordHash = requireNotNull(passwordEncoder.encode(command.password)),
                displayName = displayName,
                enabled = true,
            ).also { it.assignRole(UserRole.REGULAR_USER) }

        val savedUser =
            try {
                userRepository.saveAndFlush(user)
            } catch (error: DataIntegrityViolationException) {
                if (userRepository.existsByEmail(email)) {
                    throw EmailAlreadyRegisteredException()
                }

                throw error
            }

        return jwtTokenService.issueAccessToken(savedUser)
    }

    private fun normalizeEmail(email: String): String = email.trim().lowercase()
}

class InvalidCredentialsException : RuntimeException("Invalid email or password")

class EmailAlreadyRegisteredException : RuntimeException("Email is already registered")

class InvalidRegistrationException(
    message: String,
) : RuntimeException(message)
