package ly.music.configuration.persistence

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
            Optional.of(
                (SecurityContextHolder.getContext().authentication?.principal as? Jwt)
                    ?.getClaimAsString("email")
                    ?.takeIf { it.isNotBlank() }
                    ?: "system",
            )
        }
}
