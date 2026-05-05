package ly.music.catalog.configuration.rest

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.media.ObjectSchema
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.media.StringSchema
import org.springdoc.core.customizers.OpenApiCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfiguration {
    @Bean
    fun musiclyOpenApi(): OpenAPI =
        OpenAPI()
            .info(
                Info()
                    .title("Musicly Catalog API")
                    .version("1.0.0")
                    .description("Domain-oriented API for artists, albums, songs, versions, and tracklists.")
                    .license(License().name("Proprietary")),
            )

    @Bean
    fun resourceLinksOpenApiCustomizer(): OpenApiCustomizer =
        OpenApiCustomizer { openApi ->
            val schemas = openApi.components.schemas

            schemas["Link"] =
                ObjectSchema()
                    .description("HATEOAS link object.")
                    .addProperty("href", StringSchema().example("/artists/00000000-0000-0000-0000-000000000001"))

            schemas["ArtistLinks"] = linksSchema("Links available on an artist resource.", "self", "albums", "songs")
            schemas["AlbumLinks"] =
                linksSchema("Links available on an album resource.", "self", "artist", "album-versions")
            schemas["AlbumVersionLinks"] =
                linksSchema("Links available on an album version resource.", "self", "album", "tracks")
            schemas["AlbumVersionTrackLinks"] =
                linksSchema(
                    "Links available on an album version track resource.",
                    "self",
                    "album-version",
                    "song-version",
                )
            schemas["SongLinks"] = linksSchema("Links available on a song resource.", "self", "artist", "song-versions")
            schemas["SongVersionLinks"] = linksSchema("Links available on a song version resource.", "self", "song")

            schemas["ArtistModel"]?.properties?.set("_links", schemaRef("ArtistLinks"))
            schemas["AlbumModel"]?.properties?.set("_links", schemaRef("AlbumLinks"))
            schemas["AlbumVersionModel"]?.properties?.set("_links", schemaRef("AlbumVersionLinks"))
            schemas["AlbumVersionTrackModel"]?.properties?.set("_links", schemaRef("AlbumVersionTrackLinks"))
            schemas["SongModel"]?.properties?.set("_links", schemaRef("SongLinks"))
            schemas["SongVersionModel"]?.properties?.set("_links", schemaRef("SongVersionLinks"))
        }

    private fun linksSchema(
        description: String,
        vararg relations: String,
    ): ObjectSchema {
        val schema = ObjectSchema()
        schema.description = description
        relations.forEach { relation ->
            schema.addProperty(relation, schemaRef("Link"))
        }
        return schema
    }

    private fun schemaRef(name: String): Schema<Any> = Schema<Any>().`$ref`("#/components/schemas/$name")
}
