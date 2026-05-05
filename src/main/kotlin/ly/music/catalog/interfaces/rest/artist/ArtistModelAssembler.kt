package ly.music.catalog.interfaces.rest.artist

import ly.music.catalog.domain.artist.ArtistEntity
import ly.music.catalog.interfaces.rest.ResourceLinks
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport
import org.springframework.stereotype.Component

@Component
class ArtistModelAssembler : RepresentationModelAssemblerSupport<ArtistEntity, ArtistModel>(
    ArtistController::class.java,
    ArtistModel::class.java,
) {
    override fun instantiateModel(entity: ArtistEntity): ArtistModel =
        ArtistModel(
            name = entity.name,
            imageUrl = entity.imageUrl,
        )

    override fun toModel(entity: ArtistEntity): ArtistModel =
        createModelWithId(entity.id, entity)
            .add(
                ResourceLinks.artistAlbums(entity.id),
                ResourceLinks.artistSongs(entity.id),
            )
}
