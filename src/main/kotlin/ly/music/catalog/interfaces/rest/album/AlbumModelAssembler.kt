package ly.music.catalog.interfaces.rest.album

import ly.music.catalog.domain.album.AlbumEntity
import ly.music.catalog.interfaces.rest.ResourceLinks
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport
import org.springframework.stereotype.Component

@Component
class AlbumModelAssembler :
    RepresentationModelAssemblerSupport<AlbumEntity, AlbumModel>(
        AlbumController::class.java,
        AlbumModel::class.java,
    ) {
    override fun instantiateModel(entity: AlbumEntity): AlbumModel =
        AlbumModel(
            title = entity.title,
            releasedAt = entity.releasedAt?.value,
            imageUrl = entity.imageUrl,
        )

    override fun toModel(entity: AlbumEntity): AlbumModel =
        createModelWithId(entity.id, entity)
            .add(
                ResourceLinks.artistById(entity.artist.id).withRel("artist"),
                ResourceLinks.albumVersions(entity.id),
            )
}
