package ly.music.catalog.interfaces.rest.shared

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Spotify social metadata.")
data class SpotifySocialModel(
    val spotify: String? = null,
)
