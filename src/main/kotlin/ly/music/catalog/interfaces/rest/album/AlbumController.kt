package ly.music.catalog.interfaces.rest.album

import ly.music.catalog.application.AlbumService
import ly.music.catalog.application.CreateAlbumCommand
import ly.music.catalog.application.UpdateAlbumCommand
import ly.music.catalog.domain.release.ReleasedAt
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
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

    @PostMapping
    fun createAlbum(
        @RequestBody request: CreateOrUpdateAlbumRequest,
    ): ResponseEntity<AlbumModel> {
        val album =
            albumService.create(
                CreateAlbumCommand(
                    artistIds = request.artists.map { it.uuid() }.toSet(),
                    title = request.title,
                    releasedAt = ReleasedAt.parseOrNull(request.releasedAt),
                    imageUrl = request.imageUrl,
                ),
            )
        return ResponseEntity
            .created(
                linkTo(methodOn(AlbumController::class.java).getAlbum(album.id)).toUri(),
            ).body(albumModelAssembler.toModel(album))
    }

    @PutMapping("/{id}")
    fun updateAlbum(
        @PathVariable id: UUID,
        @RequestBody request: CreateOrUpdateAlbumRequest,
    ): ResponseEntity<AlbumModel> =
        ResponseEntity.ok(
            albumModelAssembler.toModel(
                albumService.update(
                    UpdateAlbumCommand(
                        id = id,
                        artistIds = request.artists.map { it.uuid() }.toSet(),
                        title = request.title,
                        releasedAt = ReleasedAt.parseOrNull(request.releasedAt),
                        imageUrl = request.imageUrl,
                    ),
                ),
            ),
        )

    @DeleteMapping("/{id}")
    fun deleteAlbum(
        @PathVariable id: UUID,
    ): ResponseEntity<Void> {
        albumService.delete(id)
        return ResponseEntity.noContent().build()
    }
}
