package ly.music.auth

import ly.music.auth.interfaces.rest.AuthRootModel
import ly.music.auth.interfaces.rest.AuthRootResourceLinks
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
                AuthRootResourceLinks.register(),
            ),
        )
}
