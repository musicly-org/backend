package ly.music.catalog.interfaces.rest.albumversion

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.hateoas.RepresentationModel

@Schema(
    description = "Album version resource.",
    example = """
        {
          "title": "Deluxe Edition",
          "releasedAt": "2026-01-01",
          "imageUrl": "https://i.scdn.co/image/example",
          "isDefault": false,
          "_links": {
            "self": { "href": "/album-versions/00000000-0000-0000-0000-000000000003" },
            "album": { "href": "/albums/00000000-0000-0000-0000-000000000002" },
            "tracks": { "href": "/album-versions/00000000-0000-0000-0000-000000000003/tracks" }
          }
        }
    """,
)
data class AlbumVersionModel(
    val title: String,
    val releasedAt: String?,
    val imageUrl: String?,
    val isDefault: Boolean,
) : RepresentationModel<AlbumVersionModel>()
