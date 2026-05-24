package ly.music.catalog.configuration.rest

import jakarta.servlet.http.HttpServletRequest
import ly.music.auth.application.EmailAlreadyRegisteredException
import ly.music.auth.application.InvalidCredentialsException
import ly.music.auth.application.InvalidRegistrationException
import ly.music.catalog.application.NotFoundException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.net.URI

@RestControllerAdvice
class ErrorHandler {
    @ExceptionHandler(InvalidCredentialsException::class)
    fun handleInvalidCredentials(
        error: InvalidCredentialsException,
        request: HttpServletRequest,
    ): ResponseEntity<ProblemDetail> = problem(HttpStatus.UNAUTHORIZED, error.message.orEmpty(), request)

    @ExceptionHandler(EmailAlreadyRegisteredException::class)
    fun handleEmailAlreadyRegistered(
        error: EmailAlreadyRegisteredException,
        request: HttpServletRequest,
    ): ResponseEntity<ProblemDetail> = problem(HttpStatus.CONFLICT, error.message.orEmpty(), request)

    @ExceptionHandler(InvalidRegistrationException::class)
    fun handleInvalidRegistration(
        error: InvalidRegistrationException,
        request: HttpServletRequest,
    ): ResponseEntity<ProblemDetail> = problem(HttpStatus.BAD_REQUEST, error.message.orEmpty(), request)

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(
        error: NotFoundException,
        request: HttpServletRequest,
    ): ResponseEntity<ProblemDetail> = problem(HttpStatus.NOT_FOUND, error.message.orEmpty(), request)

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleInvalidRequest(
        error: IllegalArgumentException,
        request: HttpServletRequest,
    ): ResponseEntity<ProblemDetail> = problem(HttpStatus.BAD_REQUEST, error.message.orEmpty(), request)

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleUnreadableMessage(
        error: HttpMessageNotReadableException,
        request: HttpServletRequest,
    ): ResponseEntity<ProblemDetail> {
        val detail =
            generateSequence(error as Throwable?) { it.cause }
                .filterIsInstance<IllegalArgumentException>()
                .mapNotNull { it.message }
                .firstOrNull()
                ?: "Malformed JSON request"
        return problem(HttpStatus.BAD_REQUEST, detail, request)
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpectedError(
        error: Exception,
        request: HttpServletRequest,
    ): ResponseEntity<ProblemDetail> {
        logger.error("Unhandled exception while processing {} {}", request.method, request.requestURI, error)
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", request)
    }

    private fun problem(
        status: HttpStatus,
        detail: String,
        request: HttpServletRequest,
    ): ResponseEntity<ProblemDetail> {
        val problem =
            ProblemDetail.forStatusAndDetail(status, detail).apply {
                title = status.reasonPhrase
                type = URI.create("about:blank")
                instance = URI.create(request.requestURI)
                setProperty("message", detail)
            }
        return ResponseEntity.status(status)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(problem)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ErrorHandler::class.java)
    }
}
