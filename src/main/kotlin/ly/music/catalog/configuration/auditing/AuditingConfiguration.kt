package ly.music.catalog.configuration.auditing

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import java.util.Optional

@Configuration
@EnableJpaAuditing
class AuditingConfiguration {
    @Bean
    fun auditorAware(): AuditorAware<String> =
        AuditorAware {
            (SecurityContextHolder.getContext().authentication?.principal as? Jwt)
                ?.getClaimAsString("email")
                ?.takeIf { it.isNotBlank() }
                .let { Optional.ofNullable(it ?: SYSTEM_AUDITOR) }
        }

    companion object {
        private const val SYSTEM_AUDITOR = "system"
    }
}
