package ly.music.catalog.interfaces.rest.release

import ly.music.catalog.application.CreateReleaseCommand
import ly.music.catalog.application.ReleaseService
import ly.music.catalog.application.UpdateReleaseCommand
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
@RequestMapping("/releases")
class ReleaseController(
    private val releaseService: ReleaseService,
    private val releaseModelAssembler: ReleaseModelAssembler,
) {
    @GetMapping("/{id}")
    fun getRelease(
        @PathVariable id: UUID,
    ): ResponseEntity<ReleaseModel> = ResponseEntity.ok(releaseModelAssembler.toModel(releaseService.findById(id)))

    @PostMapping
    fun createRelease(
        @RequestBody request: CreateOrUpdateReleaseRequest,
    ): ResponseEntity<ReleaseModel> {
        val release =
            releaseService.create(
                CreateReleaseCommand(
                    albumId = request.album.uuid(),
                    title = request.title,
                    releasedAt = ReleasedAt.parseOrNull(request.releasedAt),
                    imageUrl = request.imageUrl,
                    spotifyId = request.spotifyId,
                    isDefault = request.isDefault,
                ),
            )
        return ResponseEntity.created(linkTo(methodOn(ReleaseController::class.java).getRelease(release.id)).toUri()).body(releaseModelAssembler.toModel(release))
    }

    @PutMapping("/{id}")
    fun updateRelease(
        @PathVariable id: UUID,
        @RequestBody request: CreateOrUpdateReleaseRequest,
    ): ResponseEntity<ReleaseModel> =
        ResponseEntity.ok(
            releaseModelAssembler.toModel(
                releaseService.update(
                    UpdateReleaseCommand(
                        id = id,
                        title = request.title,
                        releasedAt = ReleasedAt.parseOrNull(request.releasedAt),
                        imageUrl = request.imageUrl,
                        spotifyId = request.spotifyId,
                        isDefault = request.isDefault,
                    ),
                ),
            ),
        )

    @DeleteMapping("/{id}")
    fun deleteRelease(
        @PathVariable id: UUID,
    ): ResponseEntity<Void> {
        releaseService.delete(id)
        return ResponseEntity.noContent().build()
    }
}
