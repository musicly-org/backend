package ly.music.catalog.interfaces.rest.track

import io.swagger.v3.oas.annotations.media.Schema
import ly.music.catalog.interfaces.rest.shared.SpotifySocialModel
import org.springframework.hateoas.RepresentationModel

@Schema(
    description = "Track resource.",
    example = """
        {
          "title": "Teardrop",
          "imageUrl": "https://example.test/release.jpg",
          "social": {
            "spotify": "0ABC123track"
          },
          "durationSeconds": 180,
          "releasedAt": "2026-01-01",
          "discNumber": 1,
          "trackNumber": 3,
          "_links": {
            "self": { "href": "/tracks/00000000-0000-0000-0000-000000000005" },
            "song": { "href": "/songs/00000000-0000-0000-0000-000000000004" },
            "release": { "href": "/releases/00000000-0000-0000-0000-000000000003" }
          }
        }
    """,
)
data class TrackModel(
    val title: String,
    val imageUrl: String?,
    val social: SpotifySocialModel? = null,
    val durationSeconds: Int?,
    val releasedAt: String?,
    val discNumber: Int,
    val trackNumber: Int,
) : RepresentationModel<TrackModel>()
