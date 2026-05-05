package ly.music.catalog.interfaces.rest.albumversiontrack

import ly.music.catalog.application.AlbumVersionService
import ly.music.catalog.domain.albumversiontrack.AlbumVersionTrackEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.PagedModel
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/album-versions/{albumVersionId}/tracks")
class AlbumVersionTrackForAlbumVersionController(
    private val albumVersionService: AlbumVersionService,
    private val albumVersionTrackModelAssembler: AlbumVersionTrackModelAssembler,
) {
    @GetMapping
    fun getAlbumVersionTracks(
        @PathVariable albumVersionId: UUID,
        pageable: Pageable,
        pagedResourcesAssembler: PagedResourcesAssembler<AlbumVersionTrackEntity>,
    ): PagedModel<AlbumVersionTrackModel> =
        pagedResourcesAssembler.toModel(albumVersionService.findTracks(albumVersionId, pageable), albumVersionTrackModelAssembler)
}
