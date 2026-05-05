package ly.music.catalog.interfaces.rest.songversion

import ly.music.catalog.application.SongVersionService
import ly.music.catalog.domain.songversion.SongVersionEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.PagedModel
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/songs/{songId}/versions")
class SongVersionForSongController(
    private val songVersionService: SongVersionService,
    private val songVersionModelAssembler: SongVersionModelAssembler,
) {
    @GetMapping
    fun getSongVersions(
        @PathVariable songId: UUID,
        pageable: Pageable,
        pagedResourcesAssembler: PagedResourcesAssembler<SongVersionEntity>,
    ): PagedModel<SongVersionModel> =
        pagedResourcesAssembler.toModel(songVersionService.findBySong(songId, pageable), songVersionModelAssembler)
}
