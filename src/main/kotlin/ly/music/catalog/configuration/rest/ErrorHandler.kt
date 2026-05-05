package ly.music.catalog.configuration.rest

import ly.music.catalog.application.NotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

data class ErrorModel(
    val status: Int,
    val error: String,
    val message: String,
)

@RestControllerAdvice
class ErrorHandler {
    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(error: NotFoundException): ResponseEntity<ErrorModel> =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ErrorModel(
                status = HttpStatus.NOT_FOUND.value(),
                error = HttpStatus.NOT_FOUND.reasonPhrase,
                message = error.message.orEmpty(),
            ),
        )

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleInvalidRequest(error: IllegalArgumentException): ResponseEntity<ErrorModel> =
        ResponseEntity.badRequest().body(
            ErrorModel(
                status = HttpStatus.BAD_REQUEST.value(),
                error = HttpStatus.BAD_REQUEST.reasonPhrase,
                message = error.message.orEmpty(),
            ),
        )
}
