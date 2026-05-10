package ly.music.catalog.domain.release

import ly.music.catalog.BackendControllerE2eTestSupport
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.dao.DataIntegrityViolationException

class ReleaseRepositoryConstraintTest : BackendControllerE2eTestSupport() {
    @Nested
    inner class DefaultReleaseConstraint {
        @Test
        fun rejectsMultipleDefaultReleasesForSameAlbum() {
            val artist = createArtist()
            val album = createAlbum(artist = artist)
            createRelease(album = album, title = "Original", isDefault = true)

            assertThatThrownBy {
                createRelease(album = album, title = "Deluxe", isDefault = true)
            }.isInstanceOf(DataIntegrityViolationException::class.java)
        }
    }
}
