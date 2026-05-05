package ly.music.catalog.interfaces.rest.songversion

import ly.music.catalog.application.SongVersionService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/song-versions")
class SongVersionController(
    private val songVersionService: SongVersionService,
    private val songVersionModelAssembler: SongVersionModelAssembler,
) {
    @GetMapping("/{id}")
    fun getSongVersion(
        @PathVariable id: UUID,
    ): ResponseEntity<SongVersionModel> = ResponseEntity.ok(songVersionModelAssembler.toModel(songVersionService.findById(id)))
}
