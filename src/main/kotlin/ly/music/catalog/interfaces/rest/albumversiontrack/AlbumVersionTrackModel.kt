package ly.music.catalog.interfaces.rest.albumversiontrack

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.hateoas.RepresentationModel

@Schema(
    description = "Album version track resource.",
    example = """
        {
          "discNumber": 1,
          "trackNumber": 1,
          "_links": {
            "self": { "href": "/tracks/00000000-0000-0000-0000-000000000006" },
            "album-version": { "href": "/album-versions/00000000-0000-0000-0000-000000000003" },
            "song-version": { "href": "/song-versions/00000000-0000-0000-0000-000000000005" }
          }
        }
    """,
)
data class AlbumVersionTrackModel(
    val discNumber: Int,
    val trackNumber: Int,
) : RepresentationModel<AlbumVersionTrackModel>()
