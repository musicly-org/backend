package ly.music.catalog.interfaces.rest.songversion

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.hateoas.RepresentationModel

@Schema(
    description = "Song version resource.",
    example = """
        {
          "title": "Radio Edit",
          "durationSeconds": 180,
          "releasedAt": "2026-01-01",
          "_links": {
            "self": { "href": "/song-versions/00000000-0000-0000-0000-000000000005" },
            "song": { "href": "/songs/00000000-0000-0000-0000-000000000004" }
          }
        }
    """,
)
data class SongVersionModel(
    val title: String,
    val durationSeconds: Int?,
    val releasedAt: String?,
) : RepresentationModel<SongVersionModel>()
