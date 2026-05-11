package ly.music.catalog.interfaces.rest.release

import ly.music.catalog.domain.release.ReleaseEntity
import ly.music.catalog.interfaces.rest.ResourceLinks
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport
import org.springframework.stereotype.Component

@Component
class ReleaseModelAssembler :
    RepresentationModelAssemblerSupport<ReleaseEntity, ReleaseModel>(
        ReleaseController::class.java,
        ReleaseModel::class.java,
    ) {
    override fun instantiateModel(entity: ReleaseEntity): ReleaseModel =
        ReleaseModel(
            title = entity.title,
            releasedAt = entity.releasedAt?.value,
            imageUrl = entity.imageUrl,
            isDefault = entity.isDefault,
        )

    override fun toModel(entity: ReleaseEntity): ReleaseModel =
        createModelWithId(entity.id, entity)
            .add(
                ResourceLinks.album(entity.album.id),
                ResourceLinks.releaseTracks(entity.id),
            )
}
