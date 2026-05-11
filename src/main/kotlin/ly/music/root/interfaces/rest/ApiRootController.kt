package ly.music.root.interfaces.rest

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ApiRootController {
    @GetMapping("/")
    fun getRoot(): ResponseEntity<ApiRootModel> =
        ResponseEntity.ok(
            ApiRootModel().add(
                RootResourceLinks.root(),
                RootResourceLinks.catalogRoot(),
                RootResourceLinks.authRoot(),
            ),
        )
}
