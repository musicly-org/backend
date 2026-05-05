package ly.music.catalog.interfaces.rest.albumversiontrack

import ly.music.catalog.application.AlbumVersionService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/tracks")
class AlbumVersionTrackController(
    private val albumVersionService: AlbumVersionService,
    private val albumVersionTrackModelAssembler: AlbumVersionTrackModelAssembler,
) {
    @GetMapping("/{id}")
    fun getTrack(
        @PathVariable id: UUID,
    ): ResponseEntity<AlbumVersionTrackModel> = ResponseEntity.ok(albumVersionTrackModelAssembler.toModel(albumVersionService.findTrackById(id)))
}
