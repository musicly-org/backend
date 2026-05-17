package ly.music.auth.application

data class RegisterUserCommand(
    val email: String,
    val password: String,
    val displayName: String?,
)
