package ly.music.catalog.interfaces.rest.album

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.hateoas.RepresentationModel

@Schema(
    description = "Album resource.",
    example = """
        {
          "title": "Album",
          "releasedAt": "2026",
          "imageUrl": "https://i.scdn.co/image/example",
          "_links": {
            "self": { "href": "/albums/00000000-0000-0000-0000-000000000002" },
            "artists": { "href": "/albums/00000000-0000-0000-0000-000000000002/artists" },
            "releases": { "href": "/albums/00000000-0000-0000-0000-000000000002/releases" }
          }
        }
    """,
)
data class AlbumModel(
    val title: String,
    val releasedAt: String?,
    val imageUrl: String?,
) : RepresentationModel<AlbumModel>()
