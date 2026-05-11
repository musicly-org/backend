package ly.music.catalog.interfaces.rest.track

import ly.music.catalog.application.CreateTrackCommand
import ly.music.catalog.application.TrackService
import ly.music.catalog.application.UpdateTrackCommand
import ly.music.catalog.domain.release.ReleasedAt
import ly.music.catalog.interfaces.rest.shared.TrackModelAssembler
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
@RequestMapping("/tracks")
class TrackController(
    private val trackService: TrackService,
    private val trackModelAssembler: TrackModelAssembler,
) {
    @GetMapping("/{id}")
    fun getTrack(
        @PathVariable id: UUID,
    ): ResponseEntity<TrackModel> = ResponseEntity.ok(trackModelAssembler.toModel(trackService.findById(id)))

    @PostMapping
    fun createTrack(
        @RequestBody request: CreateOrUpdateTrackRequest,
    ): ResponseEntity<TrackModel> {
        val track =
            trackService.create(
                CreateTrackCommand(
                    songId = request.song.uuid(),
                    releaseId = request.release.uuid(),
                    title = request.title,
                    imageUrl = request.imageUrl,
                    durationSeconds = request.durationSeconds,
                    releasedAt = ReleasedAt.parseOrNull(request.releasedAt),
                    discNumber = request.discNumber,
                    trackNumber = request.trackNumber,
                    spotifyId = request.spotifyId,
                ),
            )
        return ResponseEntity
            .created(
                linkTo(methodOn(TrackController::class.java).getTrack(track.id)).toUri(),
            ).body(trackModelAssembler.toModel(track))
    }

    @PutMapping("/{id}")
    fun updateTrack(
        @PathVariable id: UUID,
        @RequestBody request: CreateOrUpdateTrackRequest,
    ): ResponseEntity<TrackModel> =
        ResponseEntity.ok(
            trackModelAssembler.toModel(
                trackService.update(
                    UpdateTrackCommand(
                        id = id,
                        songId = request.song.uuid(),
                        releaseId = request.release.uuid(),
                        title = request.title,
                        imageUrl = request.imageUrl,
                        durationSeconds = request.durationSeconds,
                        releasedAt = ReleasedAt.parseOrNull(request.releasedAt),
                        discNumber = request.discNumber,
                        trackNumber = request.trackNumber,
                        spotifyId = request.spotifyId,
                    ),
                ),
            ),
        )

    @DeleteMapping("/{id}")
    fun deleteTrack(
        @PathVariable id: UUID,
    ): ResponseEntity<Void> {
        trackService.delete(id)
        return ResponseEntity.noContent().build()
    }
}
