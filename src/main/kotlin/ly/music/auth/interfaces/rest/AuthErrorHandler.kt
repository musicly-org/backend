package ly.music.auth.interfaces.rest

import ly.music.auth.application.InvalidCredentialsException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

data class AuthErrorModel(
    val status: Int,
    val error: String,
    val message: String,
)

@RestControllerAdvice
class AuthErrorHandler {
    @ExceptionHandler(InvalidCredentialsException::class)
    fun handleInvalidCredentials(error: InvalidCredentialsException): ResponseEntity<AuthErrorModel> =
        ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            AuthErrorModel(
                status = HttpStatus.UNAUTHORIZED.value(),
                error = HttpStatus.UNAUTHORIZED.reasonPhrase,
                message = error.message.orEmpty(),
            ),
        )
}
