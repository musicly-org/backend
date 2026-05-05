package ly.music.catalog.interfaces.rest.albumversiontrack

import ly.music.catalog.domain.albumversiontrack.AlbumVersionTrackEntity
import ly.music.catalog.interfaces.rest.ResourceLinks
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport
import org.springframework.stereotype.Component

@Component
class AlbumVersionTrackModelAssembler : RepresentationModelAssemblerSupport<AlbumVersionTrackEntity, AlbumVersionTrackModel>(
    AlbumVersionTrackController::class.java,
    AlbumVersionTrackModel::class.java,
) {
    override fun instantiateModel(entity: AlbumVersionTrackEntity): AlbumVersionTrackModel =
        AlbumVersionTrackModel(
            discNumber = entity.discNumber,
            trackNumber = entity.trackNumber,
        )

    override fun toModel(entity: AlbumVersionTrackEntity): AlbumVersionTrackModel =
        createModelWithId(entity.id, entity)
            .add(
                ResourceLinks.albumVersion(entity.albumVersion.id).withRel("album-version"),
                ResourceLinks.songVersion(entity.songVersion.id).withRel("song-version"),
            )
}
