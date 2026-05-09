package ly.music.catalog.interfaces.rest.release

import ly.music.catalog.application.ReleaseService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
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
}
