package ly.music.catalog

import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.dao.DataIntegrityViolationException
import java.util.UUID

class DatabaseCascadeDeleteTest : BackendControllerE2eTestSupport() {
    @Test
    fun artistDelete_shouldCascadeToArtistSocial() {
        val artist = createArtist()
        createArtistSocial(artist = artist)

        assertThat(jdbcTemplate.update("delete from catalog.artists where id = ?", artist.id)).isEqualTo(1)

        assertThat(countRows("artists")).isEqualTo(0)
        assertThat(countRows("artist_social")).isEqualTo(0)
    }

    @Test
    fun releaseDelete_shouldCascadeToReleaseSocial() {
        val artist = createArtist()
        val album = createAlbum(artist = artist)
        val release = createRelease(album = album)
        createReleaseSocial(release = release)

        assertThat(jdbcTemplate.update("delete from catalog.releases where id = ?", release.id)).isEqualTo(1)

        assertThat(countRows("releases")).isEqualTo(0)
        assertThat(countRows("release_social")).isEqualTo(0)
    }

    @Test
    fun trackDelete_shouldCascadeToTrackSocial() {
        val artist = createArtist()
        val album = createAlbum(artist = artist)
        val release = createRelease(album = album)
        val song = createSong(artist = artist)
        val track = createTrack(release = release, song = song)
        createTrackSocial(track = track)

        assertThat(jdbcTemplate.update("delete from catalog.tracks where id = ?", track.id)).isEqualTo(1)

        assertThat(countRows("tracks")).isEqualTo(0)
        assertThat(countRows("track_social")).isEqualTo(0)
    }

    @Test
    fun artistWithAlbumOrSongReferences_shouldReturnDataIntegrityViolation() {
        val artist = createArtist()
        val album = createAlbum(artist = artist)
        val release = createRelease(album = album)
        val song = createSong(artist = artist)
        val track = createTrack(release = release, song = song)
        createTrackSocial(track = track)

        jdbcTemplate.update(
            "insert into catalog.artist_social (id, created_at, created_by, updated_at, updated_by, artist_id, spotify_id) values (?, now(), 'test', now(), 'test', ?, ?)",
            UUID.randomUUID(),
            artist.id,
            "artist-spotify-id",
        )
        jdbcTemplate.update(
            "insert into catalog.release_social (id, created_at, created_by, updated_at, updated_by, release_id, spotify_id) values (?, now(), 'test', now(), 'test', ?, ?)",
            UUID.randomUUID(),
            release.id,
            "release-spotify-id",
        )

        assertThatThrownBy {
            jdbcTemplate.update("delete from catalog.artists where id = ?", artist.id)
        }.isInstanceOf(DataIntegrityViolationException::class.java)

        assertThat(countRows("artists")).isEqualTo(1)
        assertThat(countRows("artist_social")).isEqualTo(1)
        assertThat(countRows("album_artists")).isEqualTo(1)
        assertThat(countRows("song_artists")).isEqualTo(1)
        assertThat(countRows("albums")).isEqualTo(1)
        assertThat(countRows("releases")).isEqualTo(1)
        assertThat(countRows("release_social")).isEqualTo(1)
        assertThat(countRows("songs")).isEqualTo(1)
        assertThat(countRows("tracks")).isEqualTo(1)
        assertThat(countRows("track_social")).isEqualTo(1)
    }

    private fun countRows(tableName: String): Int =
        jdbcTemplate.queryForObject("select count(*) from catalog.$tableName", Int::class.java)
            ?: error("Count query failed for $tableName")
}
