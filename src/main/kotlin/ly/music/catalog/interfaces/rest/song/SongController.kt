package ly.music.catalog.interfaces.rest.song

import ly.music.catalog.application.SongService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/songs")
class SongController(
    private val songService: SongService,
    private val songModelAssembler: SongModelAssembler,
) {
    @GetMapping("/{id}")
    fun getSong(
        @PathVariable id: UUID,
    ): ResponseEntity<SongModel> = ResponseEntity.ok(songModelAssembler.toModel(songService.findById(id)))
}
