package ly.music.root.interfaces.rest

import ly.music.auth.interfaces.rest.AuthRootResourceLinks
import ly.music.catalog.interfaces.rest.root.CatalogRootResourceLinks
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder

internal object RootResourceLinks {
    fun root() = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ApiRootController::class.java).getRoot()).withSelfRel()

    fun catalogRoot() = CatalogRootResourceLinks.root().withRel("catalog")

    fun authRoot() = AuthRootResourceLinks.root().withRel("auth")
}
