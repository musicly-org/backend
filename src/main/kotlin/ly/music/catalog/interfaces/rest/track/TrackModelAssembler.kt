package ly.music.catalog.interfaces.rest.track

import ly.music.catalog.domain.track.TrackEntity
import ly.music.catalog.domain.tracksocial.TrackSocialEntity
import ly.music.catalog.interfaces.rest.ResourceLinks
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport
import org.springframework.stereotype.Component

@Component
class TrackModelAssembler :
    RepresentationModelAssemblerSupport<TrackEntity, TrackModel>(
        TrackController::class.java,
        TrackModel::class.java,
    ) {
    override fun instantiateModel(entity: TrackEntity): TrackModel =
        TrackModel(
            title = entity.title,
            imageUrl = entity.imageUrl ?: entity.release.imageUrl,
            social = entity.social?.let(::toSocialModel),
            durationSeconds = entity.durationSeconds,
            releasedAt = entity.releasedAt?.value,
            discNumber = entity.discNumber,
            trackNumber = entity.trackNumber,
        )

    override fun toModel(entity: TrackEntity): TrackModel =
        createModelWithId(entity.id, entity)
            .add(
                ResourceLinks.song(entity.song.id),
                ResourceLinks.release(entity.release.id),
            )

    private fun toSocialModel(socialEntity: TrackSocialEntity): SocialModel =
        with(socialEntity) {
            SocialModel(
                spotifyId,
            )
        }
}
