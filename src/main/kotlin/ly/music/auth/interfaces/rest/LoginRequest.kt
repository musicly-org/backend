package ly.music.auth.interfaces.rest

data class LoginRequest(
    val email: String,
    val password: String,
)
