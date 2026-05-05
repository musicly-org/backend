# AGENTS.md

## Backend Project

The backend is an independent Kotlin + Spring Boot project located in `backend/`.

Stack:

- Kotlin
- Spring Boot 4.0.6
- Spring Web
- Spring HATEOAS
- Spring Data JPA / Hibernate
- PostgreSQL
- Liquibase

## Build Rules

- Gradle files and wrapper must live inside `backend/`.
- Do not create root-level Gradle files for backend work.
- Run backend commands from `backend/`.

Common commands:

```bash
./gradlew test
./gradlew build
./gradlew bootRun
```

## Package Structure

Use this structure for backend code:

```text
configuration
domain
application
interfaces.rest
```

Guidelines:

- Spring configuration classes live under `ly.music.catalog.configuration`.
- Domain code owns business concepts and invariants.
- Each entity and its repository live in their own package under `ly.music.catalog.domain`.
- Application services orchestrate use cases.
- REST interfaces expose DTO/resource models, not entities.
- Follow DDD boundaries: domain code owns entity state changes, application code owns use-case orchestration, and `interfaces.rest` only adapts HTTP requests/responses.
- Prefer explicit application command objects for mutating use cases instead of passing several primitives through service APIs.
- Do not mutate entity fields directly from application or interface code when a domain method exists.

## Persistence Rules

- Do not expose JPA entities from controllers.
- Repositories are persistence-only and should stay in their entity's domain subpackage.
- Prefer UUID identifiers.
- All schema changes must use Liquibase migrations.
- Liquibase changelogs must be XML-based.
- Liquibase changelog files must use only the app version, starting with `1.0.0`.
- Liquibase changeSets must be atomic and use simple numeric ids within each version file, such as `1`, `2`, `3`.
- Use one changeSet per table, constraint, index, or comparable schema operation whenever possible.
- Keep Hibernate `ddl-auto` non-generating for real environments; migrations own schema changes.
- With Spring Boot 4, prefer explicit dedicated starters/modules when needed for split functionality such as Liquibase and MVC test support.

Required entities:

```text
ArtistEntity
ArtistSocialEntity
AlbumEntity
AlbumVersionEntity
AlbumVersionSocialEntity
SongEntity
SongVersionEntity
SongVersionSocialEntity
AlbumVersionTrackEntity
```

Required value objects:

```text
ReleasedAt
```

Relationship rules:

- `Artist` has many `Album`s.
- `Artist` has many `Song`s.
- `ArtistSocial` stores external identity for an `Artist`.
- `AlbumVersionSocial` stores external identity for an `AlbumVersion`.
- `SongVersionSocial` stores external identity for a `SongVersion`.
- Several artists may have the same name.
- Do not use artist name as a uniqueness boundary.
- Spotify identity belongs in `artist_social.spotify_id`.
- Spotify album identity belongs in `album_version_social.spotify_id`.
- Spotify track identity belongs in `song_version_social.spotify_id`.
- `releasedAt` stores release precision as text in `YYYY`, `YYYY-MM`, or `YYYY-MM-DD` format.
- Do not convert partial release dates to fake full dates.
- `Album` has many `AlbumVersion`s.
- Album detail requests should resolve to the default `AlbumVersion`.
- Alternate album versions should be exposed as a separate related collection.
- `Song` has many `SongVersion`s.
- `AlbumVersion` links to `SongVersion` only through `AlbumVersionTrack`.
- `AlbumVersionTrack` must include explicit track ordering.

## REST API Rules

- API resources should be domain-oriented and stable.
- Do not leak database table structure into API responses.
- Use DTOs/resource representations.
- Include Spring HATEOAS links consistently where resources are exposed.
- The API root at `/` should expose only self links for the core model resources; detailed navigation belongs on the resource representations themselves.
- Keep the OpenAPI contract available through springdoc.
- OpenAPI JSON path: `/openapi`.
- Swagger UI path: `/swagger-ui`, with `/swagger-ui/` redirecting to `/swagger-ui/index.html`.
- Update OpenAPI metadata/configuration when API title, version, or public resource behavior changes.

Required link categories:

- `self`
- relevant navigation links
- related resource links

Example:

```json
{
  "name": "Artist",
  "_links": {
    "self": { "href": "/artists/artist-id" },
    "albums": { "href": "/artists/artist-id/albums" },
    "songs": { "href": "/artists/artist-id/songs" }
  }
}
```

## Testing

- Unit test domain/application logic.
- Add integration tests for repositories and REST endpoints where behavior depends on Spring/JPA.
- End-to-end REST tests use MockMvc, AssertJ, and Testcontainers.
- Prefer meaningful behavior tests over coverage-only tests.
