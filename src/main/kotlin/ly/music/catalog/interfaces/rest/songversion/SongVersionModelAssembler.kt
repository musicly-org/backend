package ly.music.catalog.interfaces.rest.songversion

import ly.music.catalog.domain.songversion.SongVersionEntity
import ly.music.catalog.interfaces.rest.ResourceLinks
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport
import org.springframework.stereotype.Component

@Component
class SongVersionModelAssembler : RepresentationModelAssemblerSupport<SongVersionEntity, SongVersionModel>(
    SongVersionController::class.java,
    SongVersionModel::class.java,
) {
    override fun instantiateModel(entity: SongVersionEntity): SongVersionModel =
        SongVersionModel(
            title = entity.title,
            durationSeconds = entity.durationSeconds,
            releasedAt = entity.releasedAt?.value,
        )

    override fun toModel(entity: SongVersionEntity): SongVersionModel =
        createModelWithId(entity.id, entity)
            .add(
                ResourceLinks.song(entity.song.id).withRel("song"),
            )
}
