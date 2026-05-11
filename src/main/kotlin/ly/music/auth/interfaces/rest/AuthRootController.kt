package ly.music.auth.interfaces.rest

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthRootController {
    @GetMapping
    fun getRoot(): ResponseEntity<AuthRootModel> =
        ResponseEntity.ok(
            AuthRootModel().add(
                AuthRootResourceLinks.root(),
                AuthRootResourceLinks.login(),
            ),
        )
}
