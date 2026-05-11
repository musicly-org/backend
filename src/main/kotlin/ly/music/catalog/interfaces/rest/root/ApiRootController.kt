package ly.music.catalog.interfaces.rest.root

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
                RootResourceLinks.artistsTemplate(),
                RootResourceLinks.artistById(),
                RootResourceLinks.albumTemplate(),
                RootResourceLinks.releaseTemplate(),
                RootResourceLinks.songTemplate(),
                RootResourceLinks.trackTemplate(),
            ),
        )
}
