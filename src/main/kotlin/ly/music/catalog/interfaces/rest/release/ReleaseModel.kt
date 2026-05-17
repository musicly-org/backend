package ly.music.catalog.interfaces.rest.release

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import ly.music.catalog.interfaces.rest.shared.SpotifySocialModel
import org.springframework.hateoas.RepresentationModel

@Schema(
    description = "Release resource.",
    example = """
        {
          "title": "Deluxe Edition",
          "releasedAt": "2026-01-01",
          "imageUrl": "https://i.scdn.co/image/example",
          "social": {
            "spotify": "0ABC123release"
          },
          "isDefault": false,
          "_links": {
            "self": { "href": "/releases/00000000-0000-0000-0000-000000000003" },
            "album": { "href": "/albums/00000000-0000-0000-0000-000000000002" },
            "tracks": { "href": "/releases/00000000-0000-0000-0000-000000000003/tracks" }
          }
        }
    """,
)
data class ReleaseModel(
    val title: String,
    val releasedAt: String?,
    val imageUrl: String?,
    val social: SpotifySocialModel? = null,
    @get:JsonProperty("isDefault")
    val isDefault: Boolean,
) : RepresentationModel<ReleaseModel>()
