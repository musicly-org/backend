package ly.music.catalog.interfaces.rest.song

import ly.music.catalog.application.CreateSongCommand
import ly.music.catalog.application.SongService
import ly.music.catalog.application.UpdateSongCommand
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
@RequestMapping("/songs")
class SongController(
    private val songService: SongService,
    private val songModelAssembler: SongModelAssembler,
) {
    @GetMapping("/{id}")
    fun getSong(
        @PathVariable id: UUID,
    ): ResponseEntity<SongModel> = ResponseEntity.ok(songModelAssembler.toModel(songService.findById(id)))

    @PostMapping
    fun createSong(
        @RequestBody request: CreateOrUpdateSongRequest,
    ): ResponseEntity<SongModel> {
        val song =
            songService.create(
                CreateSongCommand(
                    artistIds = request.artists.map { it.uuid() }.toSet(),
                    title = request.title,
                    releasedAt = ReleasedAt.parseOrNull(request.releasedAt),
                ),
            )
        return ResponseEntity
            .created(
                linkTo(methodOn(SongController::class.java).getSong(song.id)).toUri(),
            ).body(songModelAssembler.toModel(song))
    }

    @PutMapping("/{id}")
    fun updateSong(
        @PathVariable id: UUID,
        @RequestBody request: CreateOrUpdateSongRequest,
    ): ResponseEntity<SongModel> =
        ResponseEntity.ok(
            songModelAssembler.toModel(
                songService.update(
                    UpdateSongCommand(
                        id = id,
                        artistIds = request.artists.map { it.uuid() }.toSet(),
                        title = request.title,
                        releasedAt = ReleasedAt.parseOrNull(request.releasedAt),
                    ),
                ),
            ),
        )

    @DeleteMapping("/{id}")
    fun deleteSong(
        @PathVariable id: UUID,
    ): ResponseEntity<Void> {
        songService.delete(id)
        return ResponseEntity.noContent().build()
    }
}
