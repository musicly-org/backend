package ly.music.catalog.interfaces.rest.root

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.hateoas.RepresentationModel

@Schema(
    description = "Catalog module root resource with entry points for catalog resources.",
    example = """
        {
          "_links": {
            "self": { "href": "http://localhost:8080/catalog" },
            "artists": { "href": "http://localhost:8080/artists" },
            "artist": { "href": "http://localhost:8080/artists/{id}" },
            "album": { "href": "http://localhost:8080/albums/{id}" },
            "release": { "href": "http://localhost:8080/releases/{id}" },
            "track": { "href": "http://localhost:8080/tracks/{id}" },
            "song": { "href": "http://localhost:8080/songs/{id}" }
          }
        }
    """,
)
class CatalogRootModel : RepresentationModel<CatalogRootModel>()
