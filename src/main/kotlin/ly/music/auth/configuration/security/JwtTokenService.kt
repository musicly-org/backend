package ly.music.auth.configuration.security

import ly.music.auth.domain.UserEntity
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwsHeader
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.Instant

@Service
class JwtTokenService(
    private val jwtEncoder: JwtEncoder,
    private val jwtProperties: JwtProperties,
    private val clock: Clock,
) {
    fun issueAccessToken(user: UserEntity): IssuedToken {
        val issuedAt = Instant.now(clock)
        val expiresAt = issuedAt.plus(jwtProperties.accessTokenTtl)
        val roles = user.roles.map { it.name }.sorted()
        val permissions = user.permissions.map { it.authority }.sorted()

        val claims =
            JwtClaimsSet
                .builder()
                .issuer(jwtProperties.issuer)
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .subject(user.id.toString())
                .claim("email", user.email)
                .claim("displayName", user.displayName)
                .claim("roles", roles)
                .claim("permissions", permissions)
                .build()

        val token =
            jwtEncoder.encode(
                JwtEncoderParameters.from(
                    JwsHeader.with(MacAlgorithm.HS256).build(),
                    claims,
                ),
            ).tokenValue
        return IssuedToken(
            accessToken = token,
            expiresAt = expiresAt,
            roles = roles,
            permissions = permissions,
        )
    }
}

data class IssuedToken(
    val accessToken: String,
    val expiresAt: Instant,
    val roles: List<String>,
    val permissions: List<String>,
)
