package ly.music.auth.configuration.security

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("security.bootstrap-admin")
data class BootstrapAdminProperties(
    val email: String? = null,
    val password: String? = null,
    val displayName: String? = null,
) {
    fun isConfigured(): Boolean = !email.isNullOrBlank() && !password.isNullOrBlank() && !displayName.isNullOrBlank()
}
