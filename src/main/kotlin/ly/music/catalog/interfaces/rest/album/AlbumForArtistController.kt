package ly.music.catalog.interfaces.rest.album

import ly.music.catalog.application.AlbumService
import ly.music.catalog.domain.album.AlbumEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.PagedModel
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/artists/{artistId}/albums")
class AlbumForArtistController(
    private val albumService: AlbumService,
    private val albumModelAssembler: AlbumModelAssembler,
) {
    @GetMapping
    fun getArtistAlbums(
        @PathVariable artistId: UUID,
        pageable: Pageable,
        pagedResourcesAssembler: PagedResourcesAssembler<AlbumEntity>,
    ): PagedModel<AlbumModel> =
        pagedResourcesAssembler.toModel(albumService.findByArtist(artistId, pageable), albumModelAssembler)
}
