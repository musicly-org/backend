package ly.music.catalog

import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.dao.DataIntegrityViolationException
import java.util.UUID

class DatabaseCascadeDeleteTest : BackendControllerE2eTestSupport() {
    @Test
    fun deletingArtistIsRejectedWhenAlbumsOrSongsStillReferenceIt() {
        val artist = createArtist()
        val album = createAlbum(artist = artist)
        val release = createRelease(album = album)
        val song = createSong(artist = artist)
        val track = createTrack(release = release, song = song)
        createTrackSocial(track = track)

        jdbcTemplate.update(
            "insert into artist_social (id, created_at, updated_at, artist_id, spotify_id) values (?, now(), now(), ?, ?)",
            UUID.randomUUID(),
            artist.id,
            "artist-spotify-id",
        )
        jdbcTemplate.update(
            "insert into release_social (id, created_at, updated_at, release_id, spotify_id) values (?, now(), now(), ?, ?)",
            UUID.randomUUID(),
            release.id,
            "release-spotify-id",
        )

        assertThatThrownBy {
            jdbcTemplate.update("delete from artists where id = ?", artist.id)
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
        jdbcTemplate.queryForObject("select count(*) from $tableName", Int::class.java) ?: error("Count query failed for $tableName")
}
