package ly.music.catalog.interfaces.rest.song

import ly.music.catalog.domain.song.SongEntity
import ly.music.catalog.interfaces.rest.ResourceLinks
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport
import org.springframework.stereotype.Component

@Component
class SongModelAssembler : RepresentationModelAssemblerSupport<SongEntity, SongModel>(
    SongController::class.java,
    SongModel::class.java,
) {
    override fun instantiateModel(entity: SongEntity): SongModel =
        SongModel(
            title = entity.title,
            releasedAt = entity.releasedAt?.value,
        )

    override fun toModel(entity: SongEntity): SongModel =
        createModelWithId(entity.id, entity)
            .add(
                ResourceLinks.artistById(entity.artist.id).withRel("artist"),
                ResourceLinks.songVersions(entity.id),
            )
}
