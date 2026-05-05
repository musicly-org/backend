package ly.music.catalog.interfaces.rest.albumversion

import ly.music.catalog.domain.albumversion.AlbumVersionEntity
import ly.music.catalog.interfaces.rest.ResourceLinks
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport
import org.springframework.stereotype.Component

@Component
class AlbumVersionModelAssembler : RepresentationModelAssemblerSupport<AlbumVersionEntity, AlbumVersionModel>(
    AlbumVersionController::class.java,
    AlbumVersionModel::class.java,
) {
    override fun instantiateModel(entity: AlbumVersionEntity): AlbumVersionModel =
        AlbumVersionModel(
            title = entity.title,
            releasedAt = entity.releasedAt?.value,
            imageUrl = entity.imageUrl,
            isDefault = entity.isDefault,
        )

    override fun toModel(entity: AlbumVersionEntity): AlbumVersionModel =
        createModelWithId(entity.id, entity)
            .add(
                ResourceLinks.album(entity.album.id).withRel("album"),
                ResourceLinks.albumVersionTracks(entity.id),
            )
}
