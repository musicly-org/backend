package ly.music.auth.configuration.security

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("security.bootstrap-admin")
data class BootstrapAdminProperties(
    val email: String,
    val password: String,
    val displayName: String,
)
