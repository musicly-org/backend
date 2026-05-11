package ly.music.catalog.interfaces.rest.track

import ly.music.catalog.application.TrackService
import ly.music.catalog.domain.track.TrackEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.PagedModel
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/songs/{songId}/tracks")
class TrackForSongController(
    private val trackService: TrackService,
    private val trackModelAssembler: TrackModelAssembler,
) {
    @GetMapping
    fun getSongTracks(
        @PathVariable songId: UUID,
        pageable: Pageable,
        pagedResourcesAssembler: PagedResourcesAssembler<TrackEntity>,
    ): PagedModel<TrackModel> =
        pagedResourcesAssembler.toModel(trackService.findBySong(songId, pageable), trackModelAssembler)
}
