package ly.music.catalog.interfaces.rest.track

import ly.music.catalog.application.TrackService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/tracks")
class TrackController(
    private val trackService: TrackService,
    private val trackModelAssembler: TrackModelAssembler,
) {
    @GetMapping("/{id}")
    fun getTrack(
        @PathVariable id: UUID,
    ): ResponseEntity<TrackModel> = ResponseEntity.ok(trackModelAssembler.toModel(trackService.findById(id)))
}
