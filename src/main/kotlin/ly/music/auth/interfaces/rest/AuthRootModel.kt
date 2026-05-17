package ly.music.auth.interfaces.rest

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.hateoas.RepresentationModel

@Schema(
    description = "Auth module root resource with entry points for authentication endpoints.",
    example = """
        {
          "_links": {
            "self": { "href": "http://localhost:8080/auth" },
            "login": { "href": "http://localhost:8080/auth/login" },
            "register": { "href": "http://localhost:8080/auth/register" }
          }
        }
    """,
)
class AuthRootModel : RepresentationModel<AuthRootModel>()
