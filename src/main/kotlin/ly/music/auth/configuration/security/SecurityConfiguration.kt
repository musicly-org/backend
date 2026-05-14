package ly.music.auth.configuration.security

import com.nimbusds.jose.jwk.source.ImmutableSecret
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.web.SecurityFilterChain
import java.nio.charset.StandardCharsets
import java.time.Clock
import javax.crypto.spec.SecretKeySpec

@Configuration
@EnableConfigurationProperties(JwtProperties::class, BootstrapAdminProperties::class)
class SecurityConfiguration {
    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
    ): SecurityFilterChain =
        http
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it.requestMatchers("/auth/**", "/openapi/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                it.requestMatchers(HttpMethod.GET, "/**").permitAll()
                it.anyRequest().hasAuthority("catalog:write")
            }.oauth2ResourceServer {
                it.jwt { jwt -> jwt.jwtAuthenticationConverter(::jwtAuthentication) }
            }.build()

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun jwtEncoder(jwtProperties: JwtProperties): JwtEncoder =
        NimbusJwtEncoder(ImmutableSecret(secretKey(jwtProperties)))

    @Bean
    fun jwtDecoder(jwtProperties: JwtProperties): JwtDecoder =
        NimbusJwtDecoder
            .withSecretKey(secretKey(jwtProperties))
            .macAlgorithm(MacAlgorithm.HS256)
            .build()

    @Bean
    fun clock(): Clock = Clock.systemUTC()

    private fun jwtAuthentication(jwt: Jwt): JwtAuthenticationToken =
        JwtAuthenticationToken(
            jwt,
            buildAuthorities(jwt),
        )

    private fun buildAuthorities(jwt: Jwt): MutableCollection<SimpleGrantedAuthority> {
        val roles = jwt.getClaimAsStringList("roles").orEmpty().map { SimpleGrantedAuthority("ROLE_$it") }
        val permissions = jwt.getClaimAsStringList("permissions").orEmpty().map(::SimpleGrantedAuthority)
        return (roles + permissions).toMutableList()
    }

    private fun secretKey(jwtProperties: JwtProperties): SecretKeySpec {
        val secretBytes = jwtProperties.secret.toByteArray(StandardCharsets.UTF_8)
        require(secretBytes.size >= 32) { "security.jwt.secret must be at least 32 bytes" }
        return SecretKeySpec(secretBytes, "HmacSHA256")
    }
}
