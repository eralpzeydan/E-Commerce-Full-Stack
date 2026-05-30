# Deployment

## Supported Local Deployment Modes

### 1) Split local processes (recommended for development)
Backend:
1. `cd back-end`
2. Provide env vars (`.env.example` as template)
3. `./mvnw spring-boot:run`

Frontend:
1. `cd front-end`
2. Create `.env.local` from `.env.local.example`
3. `npm install`
4. `npm run dev`

### 2) Backend via Docker Compose
File: `back-end/docker-compose.yml`

Services included:
- `db` (Postgres 16)
- `app` (Spring Boot jar)

Run:
1. `cd back-end`
2. `docker compose up --build`

App endpoints:
- API: `http://localhost:8080`
- Postgres: `localhost:5432`

## Environment Variables

### Backend (`back-end/.env.example`)
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `REDIS_HOST`
- `REDIS_PORT`
- `RABBITMQ_HOST`
- `RABBITMQ_PORT`
- `RABBITMQ_USERNAME`
- `RABBITMQ_PASSWORD`
- `SPRING_PROFILES_ACTIVE` (optional)

### Frontend (`front-end/.env.local.example`)
- `NEXT_PUBLIC_API_BASE_URL`

## Docker Images
Backend Dockerfile:
- `back-end/Dockerfile`

Build strategy:
- Multi-stage: Maven build stage + JRE runtime stage.
- Final artifact: `app.jar` launched on port `8080`.

## Health and Verification Checklist
After deploy, verify:
1. `GET http://localhost:8080/api/v1/health`
2. `GET http://localhost:8080/actuator/health`
3. Open frontend and load `/products`.
4. Register/login and verify authenticated call to `/orders` route.

## Production Considerations
- Replace `ddl-auto=update` with migration tooling (Flyway/Liquibase).
- Externalize JWT secret and credentials securely.
- Add TLS termination (reverse proxy / ingress).
- Separate Redis/RabbitMQ/PostgreSQL managed services.
- Add observability stack (metrics, traces, structured logs).

## Deployment Gaps to Verify
- Docker Compose currently disables Redis/RabbitMQ behavior via docker profile simplifications.
  - `TODO: verify` if production-like local profile is needed.
- No frontend container/deployment manifest is provided.
  - `TODO: verify` preferred frontend deployment target (Vercel, container, static hosting + server).
- No CI/CD pipeline files are present in repo root.
  - `TODO: verify` intended delivery pipeline.
