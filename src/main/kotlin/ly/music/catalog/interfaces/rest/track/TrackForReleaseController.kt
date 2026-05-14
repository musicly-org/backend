package ly.music.catalog.interfaces.rest.track

import ly.music.catalog.application.TrackService
import ly.music.catalog.domain.track.TrackEntity
import ly.music.catalog.interfaces.rest.shared.TrackModelAssembler
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.PagedModel
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/releases/{releaseId}/tracks")
class TrackForReleaseController(
    private val trackService: TrackService,
    private val trackModelAssembler: TrackModelAssembler,
) {
    @GetMapping
    fun getReleaseTracks(
        @PathVariable releaseId: UUID,
        pageable: Pageable,
        pagedResourcesAssembler: PagedResourcesAssembler<TrackEntity>,
    ): PagedModel<TrackModel> = pagedResourcesAssembler.toModel(trackService.findByRelease(releaseId, pageable), trackModelAssembler)
}
