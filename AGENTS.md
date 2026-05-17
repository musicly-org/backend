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
./gradlew ktlintFormat
./gradlew ktlintCheck
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
- Base persisted entities include `createdAt`, `createdBy`, `updatedAt`, and `updatedBy`.
- Audit user values should be populated through Spring Data auditing, using the authenticated user when available and `system` otherwise.

Required entities:

```text
ArtistEntity
ArtistSocialEntity
AlbumEntity
ReleaseEntity
ReleaseSocialEntity
SongEntity
TrackEntity
TrackSocialEntity
```

Required value objects:

```text
ReleasedAt
```

Relationship rules:

- `Album` has one or more `Artist`s.
- `Song` has one or more `Artist`s.
- `ArtistSocial` stores external identity for an `Artist`.
- `ReleaseSocial` stores external identity for a `Release`.
- `TrackSocial` stores external identity for a `Track`.
- Several artists may have the same name.
- Do not use artist name as a uniqueness boundary.
- Spotify identity belongs in `artist_social.spotify_id`.
- Spotify release identity belongs in `release_social.spotify_id`.
- Spotify track identity belongs in `track_social.spotify_id`.
- `releasedAt` stores release precision as text in `YYYY`, `YYYY-MM`, or `YYYY-MM-DD` format.
- Do not convert partial release dates to fake full dates.
- `Album` has many `Release`s.
- Album detail requests should resolve to the default `Release`.
- Alternate releases should be exposed as a separate related collection.
- `Song` has many `Track`s.
- `Release` has many `Track`s.
- Album and song resource representations should link to related artists via the `artists` relation.
- `Track` must include explicit track ordering.

## REST API Rules

- API resources should be domain-oriented and stable.
- Do not leak database table structure into API responses.
- Use DTOs/resource representations.
- Include Spring HATEOAS links consistently where resources are exposed.
- Mutating request DTOs should use link-oriented relation input through `_links` rather than raw related ids.
- Prefer concrete request names such as `CreateOrUpdateSongRequest`; avoid alias-only request type names.
- The API root at `/` is a top-level module entry point and currently exposes:
  - `self`
  - `catalog`
  - `auth`
- The catalog module root at `/catalog` exposes the catalog entry points for core model resources such as `artists`, `artist`, `album`, `release`, `song`, and `track`.
- Keep documentation and tests aligned with this split between the global API root and the catalog module root.
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

Mutation request example:

```json
{
  "title": "Mezzanine",
  "releasedAt": "1998",
  "_links": {
    "artists": [
      { "href": "/artists/00000000-0000-0000-0000-000000000001" }
    ]
  }
}
```

## Testing

- Run `./gradlew ktlintFormat` before finishing Kotlin changes when formatting is needed.
- Run `./gradlew ktlintCheck` as the default Kotlin style verification step.
- Unit test domain/application logic.
- Add integration tests for repositories and REST endpoints where behavior depends on Spring/JPA.
- End-to-end REST tests use MockMvc, AssertJ, and Testcontainers.
- Prefer meaningful behavior tests over coverage-only tests.
