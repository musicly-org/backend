# Local Development

Start PostgreSQL:

```bash
docker compose -f localdev/docker-compose.yml up -d
```

Run the backend:

```bash
cd backend
./gradlew bootRun --args='--spring.profiles.active=localdev'
```

Backend devtools is enabled in this profile, so file changes trigger restart and LiveReload on port `35729`.

Run the frontend:

```bash
cd frontend
npm install
npm run dev
```

The frontend is a Next.js app and runs on `http://localhost:3000` by default.

Run the Spotify extractor:

```bash
cd spotify-extractor
SPOTIFY_CLIENT_ID=your-client-id \
SPOTIFY_CLIENT_SECRET=your-client-secret \
./gradlew bootRun --args='--spring.profiles.active=localdev'
```

Stop PostgreSQL:

```bash
docker compose -f localdev/docker-compose.yml down
```

Delete local database data:

```bash
docker compose -f localdev/docker-compose.yml down -v
rm -rf localdev/postgres-data
```

PostgreSQL 18 stores data in a major-version subdirectory. If the container fails with an `unused mount/volume` message, stop it and remove `localdev/postgres-data` so Docker can recreate the directory with the current layout.
