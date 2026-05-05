package ly.music.catalog

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import ly.music.catalog.domain.album.AlbumEntity
import ly.music.catalog.domain.album.AlbumRepository
import ly.music.catalog.domain.albumversion.AlbumVersionEntity
import ly.music.catalog.domain.albumversion.AlbumVersionRepository
import ly.music.catalog.domain.albumversiontrack.AlbumVersionTrackEntity
import ly.music.catalog.domain.albumversiontrack.AlbumVersionTrackRepository
import ly.music.catalog.domain.artist.ArtistEntity
import ly.music.catalog.domain.artist.ArtistRepository
import ly.music.catalog.domain.release.ReleasedAt
import ly.music.catalog.domain.song.SongEntity
import ly.music.catalog.domain.song.SongRepository
import ly.music.catalog.domain.songversion.SongVersionEntity
import ly.music.catalog.domain.songversion.SongVersionRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.containers.PostgreSQLContainer
import java.net.URI

@SpringBootTest
@AutoConfigureMockMvc
abstract class BackendControllerE2eTestSupport {
    @Autowired
    protected lateinit var mockMvc: MockMvc

    protected val objectMapper: ObjectMapper = jacksonObjectMapper().findAndRegisterModules()

    @Autowired
    protected lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    protected lateinit var artistRepository: ArtistRepository

    @Autowired
    protected lateinit var albumRepository: AlbumRepository

    @Autowired
    protected lateinit var albumVersionRepository: AlbumVersionRepository

    @Autowired
    protected lateinit var songRepository: SongRepository

    @Autowired
    protected lateinit var songVersionRepository: SongVersionRepository

    @Autowired
    protected lateinit var albumVersionTrackRepository: AlbumVersionTrackRepository

    @BeforeEach
    fun clearDatabase() {
        jdbcTemplate.execute(
            """
            TRUNCATE TABLE
              album_version_tracks,
              song_version_social,
              album_version_social,
              artist_social,
              song_versions,
              album_versions,
              songs,
              albums,
              artists
            CASCADE
            """.trimIndent(),
        )
    }

    protected fun getJson(path: String): JsonNode {
        val response = mockMvc.perform(get(localUri(path))).andExpect(status().isOk()).andReturn().response
        return objectMapper.readTree(response.contentAsByteArray)
    }

    protected fun postJson(
        path: String,
        payload: Any,
    ): MvcResult =
        mockMvc.perform(
            post(localUri(path))
                .contentType("application/json")
                .content(objectMapper.writeValueAsBytes(payload)),
        ).andReturn()

    private fun localUri(path: String): URI = URI.create("http://localhost:8080$path")

    protected fun embeddedItems(node: JsonNode): JsonNode {
        val embedded = node["_embedded"]
        assertThat(embedded.fieldNames().asSequence().toList()).containsExactly("content")
        return embedded["content"]
    }

    protected fun link(node: JsonNode, rel: String): String = node["_links"][rel]["href"].asText()

    protected fun assertNoId(node: JsonNode) {
        assertThat(node.get("id")).isNull()
    }

    protected fun createArtist(
        name: String = "Massive Attack",
        imageUrl: String? = "https://example.test/artist.jpg",
    ): ArtistEntity = artistRepository.saveAndFlush(ArtistEntity(name = name, imageUrl = imageUrl))

    protected fun createAlbum(
        artist: ArtistEntity,
        title: String = "Mezzanine",
        releasedAt: String? = "1998",
        imageUrl: String? = "https://example.test/album.jpg",
    ): AlbumEntity =
        albumRepository.saveAndFlush(
            AlbumEntity(
                artist = artist,
                title = title,
                releasedAt = releasedAt?.let(ReleasedAt::parse),
                imageUrl = imageUrl,
            ),
        )

    protected fun createAlbumVersion(
        album: AlbumEntity,
        title: String = "Mezzanine",
        releasedAt: String? = "1998-04-20",
        imageUrl: String? = "https://example.test/album-version.jpg",
        isDefault: Boolean = false,
    ): AlbumVersionEntity =
        albumVersionRepository.saveAndFlush(
            AlbumVersionEntity(
                album = album,
                title = title,
                releasedAt = releasedAt?.let(ReleasedAt::parse),
                imageUrl = imageUrl,
                isDefault = isDefault,
            ),
        )

    protected fun createSong(
        artist: ArtistEntity,
        title: String = "Teardrop",
        releasedAt: String? = "1998-04",
    ): SongEntity =
        songRepository.saveAndFlush(
            SongEntity(
                artist = artist,
                title = title,
                releasedAt = releasedAt?.let(ReleasedAt::parse),
            ),
        )

    protected fun createSongVersion(
        song: SongEntity,
        title: String = "Teardrop",
        durationSeconds: Int? = 330,
        releasedAt: String? = "1998-04-20",
    ): SongVersionEntity =
        songVersionRepository.saveAndFlush(
            SongVersionEntity(
                song = song,
                title = title,
                durationSeconds = durationSeconds,
                releasedAt = releasedAt?.let(ReleasedAt::parse),
            ),
        )

    protected fun createTrack(
        albumVersion: AlbumVersionEntity,
        songVersion: SongVersionEntity,
        discNumber: Int = 1,
        trackNumber: Int = 1,
    ): AlbumVersionTrackEntity =
        albumVersionTrackRepository.saveAndFlush(
            AlbumVersionTrackEntity(
                albumVersion = albumVersion,
                songVersion = songVersion,
                discNumber = discNumber,
                trackNumber = trackNumber,
            ),
        )

    companion object {
        val postgres: PostgreSQLContainer<*> =
            PostgreSQLContainer("postgres:18")
                .withDatabaseName("musicly_test")
                .withUsername("postgres")
                .withPassword("postgres")
                .apply { start() }

        @JvmStatic
        @DynamicPropertySource
        fun registerProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
        }
    }
}
