package ly.music.catalog

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import ly.music.auth.configuration.security.BootstrapAdminProvisioner
import ly.music.catalog.domain.album.AlbumEntity
import ly.music.catalog.domain.album.AlbumRepository
import ly.music.catalog.domain.artist.ArtistEntity
import ly.music.catalog.domain.artist.ArtistRepository
import ly.music.catalog.domain.release.ReleaseEntity
import ly.music.catalog.domain.release.ReleaseRepository
import ly.music.catalog.domain.release.ReleasedAt
import ly.music.catalog.domain.song.SongEntity
import ly.music.catalog.domain.song.SongRepository
import ly.music.catalog.domain.track.TrackEntity
import ly.music.catalog.domain.track.TrackRepository
import ly.music.catalog.domain.tracksocial.TrackSocialEntity
import ly.music.catalog.domain.tracksocial.TrackSocialRepository
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
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
    protected lateinit var releaseRepository: ReleaseRepository

    @Autowired
    protected lateinit var songRepository: SongRepository

    @Autowired
    protected lateinit var trackRepository: TrackRepository

    @Autowired
    protected lateinit var trackSocialRepository: TrackSocialRepository

    @Autowired
    protected lateinit var bootstrapAdminProvisioner: BootstrapAdminProvisioner

    @BeforeEach
    fun clearDatabase() {
        jdbcTemplate.execute(
            """
            TRUNCATE TABLE
              catalog.track_social,
              catalog.release_social,
              catalog.song_artists,
              catalog.album_artists,
              catalog.artist_social,
              catalog.tracks,
              catalog.releases,
              catalog.songs,
              catalog.albums,
              catalog.artists,
              auth.user_roles,
              auth.users
            CASCADE
            """.trimIndent(),
        )
    }

    protected fun getJson(path: String): JsonNode {
        val response =
            mockMvc
                .perform(get(localUri(path)))
                .andExpect(status().isOk())
                .andReturn()
                .response
        return objectMapper.readTree(response.contentAsByteArray)
    }

    protected fun getResponse(path: String): MvcResult =
        mockMvc
            .perform(get(localUri(path)))
            .andReturn()

    protected fun postJson(
        path: String,
        payload: Any,
    ): MvcResult =
        mockMvc
            .perform(
                post(localUri(path))
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsBytes(payload)),
            ).andReturn()

    protected fun postJsonAuthorized(
        path: String,
        payload: Any,
        bearerToken: String,
    ): MvcResult =
        mockMvc
            .perform(
                post(localUri(path))
                    .header("Authorization", "Bearer $bearerToken")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsBytes(payload)),
            ).andReturn()

    protected fun putJsonAuthorized(
        path: String,
        payload: Any,
        bearerToken: String,
    ): MvcResult =
        mockMvc
            .perform(
                put(localUri(path))
                    .header("Authorization", "Bearer $bearerToken")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsBytes(payload)),
            ).andReturn()

    protected fun deleteAuthorized(
        path: String,
        bearerToken: String,
    ): MvcResult =
        mockMvc
            .perform(
                delete(localUri(path))
                    .header("Authorization", "Bearer $bearerToken"),
            ).andReturn()

    protected fun loginAsBootstrapAdmin(): String {
        bootstrapAdminProvisioner.ensurePresent()

        val response =
            postJson(
                "/auth/login",
                mapOf(
                    "email" to "admin@musicly.local",
                    "password" to "change-this-admin-password",
                ),
            )

        status().isOk().match(response)
        return objectMapper.readTree(response.response.contentAsByteArray)["accessToken"].asText()
    }

    private fun localUri(path: String): URI = URI.create("http://localhost:8080$path")

    protected fun embeddedItems(node: JsonNode): JsonNode {
        val embedded = node["_embedded"]
        assertThat(embedded.fieldNames().asSequence().toList()).containsExactly("content")
        return embedded["content"]
    }

    protected fun link(
        node: JsonNode,
        rel: String,
    ): String = node["_links"][rel]["href"].asText()

    protected fun assertNoId(node: JsonNode) {
        assertThat(node.get("id")).isNull()
    }

    protected fun createArtist(
        name: String = "Massive Attack",
        imageUrl: String? = "https://example.test/artist.jpg",
    ): ArtistEntity = artistRepository.saveAndFlush(ArtistEntity(name = name, imageUrl = imageUrl))

    protected fun createAlbum(
        artist: ArtistEntity,
        artists: Collection<ArtistEntity> = listOf(artist),
        title: String = "Mezzanine",
        releasedAt: String? = "1998",
        imageUrl: String? = "https://example.test/album.jpg",
    ): AlbumEntity =
        albumRepository.saveAndFlush(
            AlbumEntity(
                title = title,
                releasedAt = releasedAt?.let(ReleasedAt::parse),
                imageUrl = imageUrl,
            ).also { it.artists.addAll(artists) },
        )

    protected fun createRelease(
        album: AlbumEntity,
        title: String = "Mezzanine",
        releasedAt: String? = "1998-04-20",
        imageUrl: String? = "https://example.test/release.jpg",
        isDefault: Boolean = false,
    ): ReleaseEntity =
        releaseRepository.saveAndFlush(
            ReleaseEntity(
                album = album,
                title = title,
                releasedAt = releasedAt?.let(ReleasedAt::parse),
                imageUrl = imageUrl,
                isDefault = isDefault,
            ),
        )

    protected fun createSong(
        artist: ArtistEntity,
        artists: Collection<ArtistEntity> = listOf(artist),
        title: String = "Teardrop",
        releasedAt: String? = "1998-04",
    ): SongEntity =
        songRepository.saveAndFlush(
            SongEntity(
                title = title,
                releasedAt = releasedAt?.let(ReleasedAt::parse),
            ).also { it.artists.addAll(artists) },
        )

    protected fun createTrack(
        release: ReleaseEntity,
        song: SongEntity,
        title: String = "Teardrop",
        durationSeconds: Int? = 330,
        releasedAt: String? = "1998-04-20",
        discNumber: Int = 1,
        trackNumber: Int = 1,
    ): TrackEntity =
        trackRepository.saveAndFlush(
            TrackEntity(
                song = song,
                release = release,
                title = title,
                durationSeconds = durationSeconds,
                releasedAt = releasedAt?.let(ReleasedAt::parse),
                discNumber = discNumber,
                trackNumber = trackNumber,
            ),
        )

    protected fun createTrackSocial(
        track: TrackEntity,
        spotifyId: String = "spotify-track-id",
    ): TrackSocialEntity =
        trackSocialRepository.saveAndFlush(
            TrackSocialEntity(
                track = track,
                spotifyId = spotifyId,
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
            registry.add("security.jwt.secret") { "test-jwt-secret-value-with-32-plus-bytes" }
            registry.add("security.bootstrap-admin.email") { "admin@musicly.local" }
            registry.add("security.bootstrap-admin.password") { "change-this-admin-password" }
            registry.add("security.bootstrap-admin.display-name") { "Musicly Admin" }
        }
    }
}
