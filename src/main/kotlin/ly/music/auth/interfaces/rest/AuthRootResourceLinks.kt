package ly.music.auth.interfaces.rest

import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn

object AuthRootResourceLinks {
    fun root() = linkTo(methodOn(AuthRootController::class.java).getRoot()).withSelfRel()

    fun login() = linkTo(methodOn(AuthController::class.java).login(LoginRequest("", ""))).withRel("login")
}
