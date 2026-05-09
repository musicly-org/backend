package ly.music.catalog

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.UUID

class DatabaseCascadeDeleteTest : BackendControllerE2eTestSupport() {
    @Test
    fun deletingArtistCascadesToDependentRows() {
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

        jdbcTemplate.update("delete from artists where id = ?", artist.id)

        assertThat(countRows("artists")).isZero()
        assertThat(countRows("artist_social")).isZero()
        assertThat(countRows("album_artists")).isZero()
        assertThat(countRows("song_artists")).isZero()
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
