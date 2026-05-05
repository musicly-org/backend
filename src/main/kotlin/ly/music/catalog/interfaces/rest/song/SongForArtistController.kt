package ly.music.catalog.interfaces.rest.song

import ly.music.catalog.application.SongService
import ly.music.catalog.domain.song.SongEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.PagedModel
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/artists/{artistId}/songs")
class SongForArtistController(
    private val songService: SongService,
    private val songModelAssembler: SongModelAssembler,
) {
    @GetMapping
    fun getArtistSongs(
        @PathVariable artistId: UUID,
        pageable: Pageable,
        pagedResourcesAssembler: PagedResourcesAssembler<SongEntity>,
    ): PagedModel<SongModel> =
        pagedResourcesAssembler.toModel(songService.findByArtist(artistId, pageable), songModelAssembler)
}
