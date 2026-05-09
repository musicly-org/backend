package ly.music.catalog.interfaces.rest.song

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.hateoas.RepresentationModel

@Schema(
    description = "Song resource.",
    example = """
        {
          "title": "Song",
          "releasedAt": "2026-01",
          "_links": {
            "self": { "href": "/songs/00000000-0000-0000-0000-000000000004" },
            "artists": { "href": "/songs/00000000-0000-0000-0000-000000000004/artists" },
            "tracks": { "href": "/songs/00000000-0000-0000-0000-000000000004/tracks" }
          }
        }
    """,
)
data class SongModel(
    val title: String,
    val releasedAt: String?,
) : RepresentationModel<SongModel>()
