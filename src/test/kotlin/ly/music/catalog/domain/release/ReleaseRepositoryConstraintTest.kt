package ly.music.catalog.domain.release

import ly.music.catalog.BackendControllerE2eTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.dao.DataIntegrityViolationException

class ReleaseRepositoryConstraintTest : BackendControllerE2eTestSupport() {
    @Nested
    inner class DuplicateTitleConstraint {
        @Test
        fun duplicateTitlesForSameAlbum_shouldBeAllowed() {
            val artist = createArtist()
            val album = createAlbum(artist = artist)

            createRelease(album = album, title = "Original", isDefault = true)
            createRelease(album = album, title = "Original")

            assertThat(releaseRepository.findByAlbumId(album.id, org.springframework.data.domain.Pageable.unpaged()).totalElements)
                .isEqualTo(2)
        }
    }

    @Nested
    inner class DefaultReleaseConstraint {
        @Test
        fun multipleDefaultReleasesForSameAlbum_shouldReturnDataIntegrityViolation() {
            val artist = createArtist()
            val album = createAlbum(artist = artist)
            createRelease(album = album, title = "Original", isDefault = true)

            assertThatThrownBy {
                createRelease(album = album, title = "Deluxe", isDefault = true)
            }.isInstanceOf(DataIntegrityViolationException::class.java)
        }
    }
}
