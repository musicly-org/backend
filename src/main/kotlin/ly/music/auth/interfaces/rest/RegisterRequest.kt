package ly.music.auth.interfaces.rest

data class RegisterRequest(
    val email: String,
    val password: String,
    val displayName: String? = null,
)
