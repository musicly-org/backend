package ly.music.catalog.interfaces.rest.artist

import ly.music.catalog.application.ArtistService
import ly.music.catalog.domain.artist.ArtistEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.PagedModel
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/albums/{albumId}/artists")
class ArtistForAlbumController(
    private val artistService: ArtistService,
    private val artistModelAssembler: ArtistModelAssembler,
) {
    @GetMapping
    fun getAlbumArtists(
        @PathVariable albumId: UUID,
        pageable: Pageable,
        pagedResourcesAssembler: PagedResourcesAssembler<ArtistEntity>,
    ): PagedModel<ArtistModel> =
        pagedResourcesAssembler.toModel(artistService.findByAlbum(albumId, pageable), artistModelAssembler)
}
