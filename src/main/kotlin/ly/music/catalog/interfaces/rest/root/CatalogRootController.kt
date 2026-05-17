package ly.music.catalog.interfaces.rest.root

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/catalog")
class CatalogRootController {
    @GetMapping
    fun getRoot(): ResponseEntity<CatalogRootModel> =
        ResponseEntity.ok(
            CatalogRootModel().add(
                CatalogRootResourceLinks.root(),
                CatalogRootResourceLinks.artistsTemplate(),
                CatalogRootResourceLinks.artistById(),
                CatalogRootResourceLinks.albumTemplate(),
                CatalogRootResourceLinks.releaseTemplate(),
                CatalogRootResourceLinks.songTemplate(),
                CatalogRootResourceLinks.trackTemplate(),
            ),
        )
}
