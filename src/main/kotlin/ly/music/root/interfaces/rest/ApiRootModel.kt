package ly.music.root.interfaces.rest

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.hateoas.RepresentationModel

@Schema(
    description = "API root resource exposing top-level module entry points.",
    example = """
        {
          "_links": {
            "self": { "href": "/" },
            "catalog": { "href": "http://localhost:8080/catalog" },
            "auth": { "href": "http://localhost:8080/auth" }
          }
        }
    """,
)
class ApiRootModel : RepresentationModel<ApiRootModel>()
