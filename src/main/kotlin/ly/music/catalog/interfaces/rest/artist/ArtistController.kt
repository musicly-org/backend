package ly.music.catalog.interfaces.rest.artist

import ly.music.catalog.application.ArtistService
import ly.music.catalog.application.CreateArtistCommand
import ly.music.catalog.domain.artist.ArtistEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.PagedModel
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/artists")
class ArtistController(
    private val artistService: ArtistService,
    private val artistModelAssembler: ArtistModelAssembler,
) {
    @GetMapping
    fun getArtists(
        pageable: Pageable,
        pagedResourcesAssembler: PagedResourcesAssembler<ArtistEntity>,
    ): PagedModel<ArtistModel> = pagedResourcesAssembler.toModel(artistService.findPage(pageable), artistModelAssembler)

    @GetMapping("/{id}")
    fun getArtistById(
        @PathVariable id: UUID,
    ): ResponseEntity<ArtistModel> = ResponseEntity.ok(artistModelAssembler.toModel(artistService.findById(id)))

    @PostMapping
    fun createArtist(
        @RequestBody request: CreateArtistRequest,
    ): ResponseEntity<ArtistModel> {
        val artist = artistService.create(CreateArtistCommand(request.name))
        val model = artistModelAssembler.toModel(artist)
        return ResponseEntity.created(linkTo(methodOn(ArtistController::class.java).getArtistById(artist.id)).toUri()).body(model)
    }
}
