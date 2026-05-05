package ly.music.catalog.interfaces.rest.artist

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.hateoas.RepresentationModel

@Schema(
    description = "Artist resource.",
    example = """
        {
          "name": "Artist",
          "imageUrl": "https://i.scdn.co/image/example",
          "_links": {
            "self": { "href": "/artists/00000000-0000-0000-0000-000000000001" },
            "albums": { "href": "/artists/00000000-0000-0000-0000-000000000001/albums" },
            "songs": { "href": "/artists/00000000-0000-0000-0000-000000000001/songs" }
          }
        }
    """,
)
data class ArtistModel(
    val name: String,
    val imageUrl: String?,
) : RepresentationModel<ArtistModel>()
