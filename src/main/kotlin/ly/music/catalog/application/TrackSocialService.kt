package ly.music.catalog.application

import ly.music.catalog.domain.tracksocial.TrackSocialEntity
import ly.music.catalog.domain.tracksocial.TrackSocialRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class TrackSocialService(
    private val trackService: TrackService,
    private val trackSocialRepository: TrackSocialRepository,
) {
    @Transactional(readOnly = true)
    fun findByTrackId(trackId: UUID): TrackSocialEntity {
        trackService.findById(trackId)

        return trackSocialRepository.findByTrackId(trackId)
            ?: throw NotFoundException("TrackSocial", trackId)
    }
}
