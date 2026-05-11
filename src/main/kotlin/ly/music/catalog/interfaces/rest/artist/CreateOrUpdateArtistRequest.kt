package ly.music.catalog.interfaces.rest.artist

data class CreateOrUpdateArtistRequest(
    val name: String,
    val imageUrl: String? = null,
    val spotifyId: String? = null,
)
