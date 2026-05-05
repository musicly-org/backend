package ly.music.catalog.interfaces.rest.albumversion

import ly.music.catalog.application.AlbumVersionService
import ly.music.catalog.domain.albumversion.AlbumVersionEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.PagedModel
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/albums/{albumId}/versions")
class AlbumVersionForAlbumController(
    private val albumVersionService: AlbumVersionService,
    private val albumVersionModelAssembler: AlbumVersionModelAssembler,
) {
    @GetMapping
    fun getAlbumVersions(
        @PathVariable albumId: UUID,
        pageable: Pageable,
        pagedResourcesAssembler: PagedResourcesAssembler<AlbumVersionEntity>,
    ): PagedModel<AlbumVersionModel> =
        pagedResourcesAssembler.toModel(albumVersionService.findByAlbum(albumId, pageable), albumVersionModelAssembler)
}
