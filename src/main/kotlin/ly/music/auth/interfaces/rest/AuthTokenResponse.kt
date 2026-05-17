package ly.music.auth.interfaces.rest

data class AuthTokenResponse(
    val accessToken: String,
    val tokenType: String,
    val expiresInSeconds: Long,
    val roles: List<String>,
    val permissions: List<String>,
)
