package ly.music.auth.configuration.security

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties("security.jwt")
data class JwtProperties(
    val secret: String,
    val issuer: String,
    val accessTokenTtl: Duration,
)
