package ly.music.catalog.domain.artist

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ArtistRepository : JpaRepository<ArtistEntity, UUID> {
    fun existsByNameIgnoreCase(name: String): Boolean
}
