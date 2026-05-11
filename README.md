# Musicly Backend

This module is the backend API for Musicly, a discography application.

The backend is responsible for:

- storing the catalog domain in PostgreSQL
- enforcing business invariants at the service and database level
- exposing stable REST resources for the frontend and other clients
- exposing create, update, and delete operations for artists, albums, songs, releases, and tracks
- representing discography data in domain terms instead of persistence-table terms

## Business Domain

Musicly models music catalog data with a strict distinction between abstract works and release-specific instances.

Core concepts:

- `Artist`: a creator or performer
- `Album`: an abstract release work
- `Release`: a concrete edition of an album
- `Song`: an abstract composition
- `Track`: a concrete appearance of a song on a release

This distinction is important:

- an `Album` is not the same thing as a `Release`
- a `Song` is not the same thing as a `Track`
- a `Release` belongs to exactly one `Album`
- a `Track` belongs to exactly one `Song` and exactly one `Release`

Example:

- `Mezzanine` is an `Album`
- `Mezzanine (1998 original CD)` and `Mezzanine (deluxe remaster)` are different `Release`s
- `Teardrop` is a `Song`
- track 3 on the original release and track 5 on a reissue can be different `Track` rows that point to the same `Song`

## Entity Model

The main persisted entities are:

- `ArtistEntity`
- `ArtistSocialEntity`
- `AlbumEntity`
- `ReleaseEntity`
- `ReleaseSocialEntity`
- `SongEntity`
- `TrackEntity`
- `TrackSocialEntity`

External identities are stored separately from the core entities:

- Spotify artist ID is stored in `artist_social.spotify_id`
- Spotify release ID is stored in `release_social.spotify_id`
- Spotify track ID is stored in `track_social.spotify_id`

This keeps external platform identity separate from Musicly domain identity.

## Core Business Rules

### Artists

- Artist names are normalized before persistence.
- Artist names are not a safe global identity boundary.
- An artist can be linked to many albums and many songs.
- Deleting an artist is blocked when albums or songs still reference that artist.

The last rule is enforced to preserve the domain invariant that albums and songs must always have at least one artist.

### Albums

- Every album must have one or more artists.
- Creating an album automatically creates its first release.
- That first release is marked as the default release.
- Album title and release date are stored at the abstract album level.

An album is the main discography object exposed to users. Releases represent concrete editions under it.

### Releases

- Every release belongs to exactly one album.
- Release titles are unique within an album.
- Exactly one default release is allowed per album.
- If a non-default release is deleted, no replacement logic is needed.
- If the default release is deleted, the oldest non-default release is promoted to default.
- Deleting the only release of an album is rejected.

The single-default invariant is enforced at the database level with a partial unique index on `releases(album_id)` where `is_default = true`.

### Songs

- Every song must have one or more artists.
- A song is the abstract composition, not a release-specific track row.
- Song title and release date are stored at song level.

Songs are linked to artists directly and to releases only through tracks.

### Tracks

- Every track belongs to one song and one release.
- A release can have many tracks.
- A song can have many tracks across different releases.
- Track ordering is explicit through `discNumber` and `trackNumber`.
- Track position must be unique within a release.
- Track responses may expose release artwork as fallback when the track itself has no image.

Tracks are the concrete units used for tracklists.

## Release Date Semantics

Release dates use the `ReleasedAt` value object.

Supported precision:

- `YYYY`
- `YYYY-MM`
- `YYYY-MM-DD`

Important behavior:

- partial precision is preserved
- fake full dates are not generated from partial values
- validation exists in both application code and database constraints

## Persistence Invariants

The backend intentionally pushes key rules into the database so the invalid state cannot be persisted by concurrency, manual SQL, or partial application bugs.

Examples of enforced invariants:

- unique release title per album
- unique track position per release
- one default release per album
- unique social identity rows per owning entity
- positive track numbering constraints
- validated `released_at` format
- foreign keys for all aggregate relationships
- audit columns for creator/updater identity on persisted rows

Artist deletion behavior is also enforced at the database boundary:

- deleting an album cascades its album-artist links
- deleting a song cascades its song-artist links
- deleting an artist does not cascade those links and therefore fails when references still exist

## Application Behavior

Business use cases are orchestrated in `ly.music.catalog.application`.

Important flows:

- `AlbumService.create`: resolves artists, creates album, creates the initial default release
- `ReleaseService.create`: creates an additional release and marks it default only when the album has no default yet
- `ReleaseService.findDefaultByAlbum`: returns the designated default release, or falls back to the first available release if needed
- `ReleaseService.delete`: promotes another release when deleting the default release
- `SongService.create`: resolves artist IDs and rejects empty artist sets
- `TrackService.create` / `TrackService.update`: enforce unique track position within a release and maintain Spotify track linkage
- `ArtistService.delete`: attempts deletion, which the database rejects if albums or songs are left without artists

## API Shape

The backend exposes domain-oriented REST resources with Spring HATEOAS.

Core resource relations:

- `artists`
- `albums`
- `releases`
- `songs`
- `tracks`

The API is intended to describe the catalog as a graph of related resources:

- artist resources link to albums and songs
- album resources link to artists and releases
- release resources link to tracks
- song resources link to artists and tracks
- track resources link back to song and release

Write requests follow the same graph-oriented approach:

- create and update payloads use `_links` to reference related resources
- request DTOs derive typed convenience properties from `_links`
- request bodies do not carry raw foreign-key ids for related catalog resources

OpenAPI endpoints:

- JSON: `/openapi`
- Swagger UI: `/swagger-ui`

Audit behavior:

- persisted rows include `created_by` and `updated_by`
- authenticated writes use the JWT `email` claim for auditing
- non-authenticated/system writes fall back to `system`

## Architectural Notes

The backend follows a simple layered structure:

- `domain`: entities, value objects, repositories, domain state changes
- `application`: use-case orchestration
- `interfaces.rest`: HTTP controllers and resource models
- `configuration`: Spring and infrastructure configuration

Design intent:

- business concepts stay in the domain language
- controllers do not expose JPA entities directly
- persistence details do not define the public API
- schema changes go through Liquibase migrations

## Summary

The central business idea in Musicly is that discography data must preserve the difference between:

- work and edition
- composition and track appearance
- internal catalog identity and external platform identity

Most of the backend rules exist to keep those distinctions intact while ensuring the database never accepts catalog states that violate them.
