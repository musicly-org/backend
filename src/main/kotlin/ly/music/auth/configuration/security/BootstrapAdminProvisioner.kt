package ly.music.auth.configuration.security

import ly.music.auth.domain.UserEntity
import ly.music.auth.domain.UserRepository
import ly.music.auth.domain.UserRole
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class BootstrapAdminProvisioner(
    private val bootstrapAdminProperties: BootstrapAdminProperties,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    fun ensurePresent(): UserEntity {
        check(bootstrapAdminProperties.isConfigured()) { "Bootstrap admin is not fully configured" }

        val email = requireNotNull(bootstrapAdminProperties.email).trim().lowercase()
        userRepository.findByEmail(email)?.let { return it }

        return userRepository.saveAndFlush(
            UserEntity(
                email = email,
                passwordHash = requireNotNull(passwordEncoder.encode(requireNotNull(bootstrapAdminProperties.password))),
                displayName = requireNotNull(bootstrapAdminProperties.displayName),
                enabled = true,
            ).also { it.roles += UserRole.SUPER_ADMIN },
        )
    }

    @Component
    class Initializer(
        private val bootstrapAdminProvisioner: BootstrapAdminProvisioner,
        private val bootstrapAdminProperties: BootstrapAdminProperties,
    ) : ApplicationRunner {
        override fun run(args: ApplicationArguments) {
            if (bootstrapAdminProperties.isConfigured()) {
                bootstrapAdminProvisioner.ensurePresent()
            }
        }
    }
}
