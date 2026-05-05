package ly.music.catalog.interfaces.rest.albumversion

import ly.music.catalog.application.AlbumVersionService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/album-versions")
class AlbumVersionController(
    private val albumVersionService: AlbumVersionService,
    private val albumVersionModelAssembler: AlbumVersionModelAssembler,
) {
    @GetMapping("/{id}")
    fun getAlbumVersion(
        @PathVariable id: UUID,
    ): ResponseEntity<AlbumVersionModel> = ResponseEntity.ok(albumVersionModelAssembler.toModel(albumVersionService.findById(id)))
}
