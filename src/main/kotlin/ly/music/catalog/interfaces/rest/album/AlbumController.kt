package ly.music.catalog.interfaces.rest.album

import ly.music.catalog.application.AlbumService
import ly.music.catalog.application.ReleaseService
import ly.music.catalog.interfaces.rest.ResourceLinks
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.util.UUID

@RestController
@RequestMapping("/albums")
class AlbumController(
    private val albumService: AlbumService,
    private val albumModelAssembler: AlbumModelAssembler,
) {
    @GetMapping("/{id}")
    fun getAlbum(
        @PathVariable id: UUID,
    ): ResponseEntity<AlbumModel> = ResponseEntity.ok(albumModelAssembler.toModel(albumService.findById(id)))
}
