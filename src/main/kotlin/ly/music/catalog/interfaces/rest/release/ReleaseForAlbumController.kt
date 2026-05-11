package ly.music.catalog.interfaces.rest.release

import ly.music.catalog.application.ReleaseService
import ly.music.catalog.domain.release.ReleaseEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.PagedModel
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/albums/{albumId}/releases")
class ReleaseForAlbumController(
    private val releaseService: ReleaseService,
    private val releaseModelAssembler: ReleaseModelAssembler,
) {
    @GetMapping
    fun getReleases(
        @PathVariable albumId: UUID,
        pageable: Pageable,
        pagedResourcesAssembler: PagedResourcesAssembler<ReleaseEntity>,
    ): PagedModel<ReleaseModel> =
        pagedResourcesAssembler.toModel(releaseService.findByAlbum(albumId, pageable), releaseModelAssembler)
}
