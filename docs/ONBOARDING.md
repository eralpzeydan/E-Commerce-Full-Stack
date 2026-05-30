# Onboarding Guide

## 1. Prerequisites
Install locally:
- Node.js 20+ and npm
- Java 17
- Docker (optional but recommended)
- PostgreSQL 16 (if not using Docker)

## 2. Clone and Install
From repository root:

```bash
cd front-end && npm install
cd ../back-end
./mvnw -q -DskipTests dependency:go-offline
```

## 3. Configure Environment

Frontend:
1. Copy `front-end/.env.local.example` to `front-end/.env.local`
2. Set:
   - `NEXT_PUBLIC_API_BASE_URL=http://localhost:8080`

Backend:
1. Copy `back-end/.env.example` to local env source
2. Configure at minimum:
   - `DB_URL`
   - `DB_USERNAME`
   - `DB_PASSWORD`

## 4. Start Services

Option A (fastest full stack locally):
1. `cd back-end && docker compose up --build`
2. In another terminal: `cd front-end && npm run dev`

Option B (manual backend runtime):
1. Start PostgreSQL separately
2. `cd back-end && ./mvnw spring-boot:run`
3. `cd front-end && npm run dev`

## 5. Smoke Test
1. Open `http://localhost:3000/products`
2. Register at `/register`
3. Login at `/login`
4. Visit `/orders` (auth-protected backend endpoint check)
5. Visit `/cart` and confirm API connectivity

## 6. Run Tests

Frontend:
```bash
cd front-end
npm run test:run
```

Backend:
```bash
cd back-end
./mvnw test
```

Observed latest baseline:
- Frontend: 17 tests passing
- Backend: 27 tests passing

## 7. Key Files to Read First
- Frontend architecture contract: `front-end/docs/frontend-architecture-contract.md`
- Frontend API contract: `front-end/docs/api-client-layer.md`
- Frontend products screen: `front-end/src/features/products/components/products-screen.tsx`
- Backend architecture doc: `back-end/docs/architecture.md`
- Security config: `back-end/src/main/java/com/eralp/ecommerce/config/SecurityConfig.java`
- Checkout logic: `back-end/src/main/java/com/eralp/ecommerce/service/impl/OrderServiceImpl.java`
- Global errors: `back-end/src/main/java/com/eralp/ecommerce/exception/GlobalExceptionHandler.java`

## 8. Known Integration Caveats
- Register/login frontend token assumptions differ from backend register response.
  - `TODO: verify`
- Frontend defines `AUTH.ME` endpoint but backend does not implement it.
  - `TODO: verify`
- Checkout frontend payload is not consumed by backend checkout endpoint.
  - `TODO: verify`
- Admin route frontend guard checks cookie existence only.
  - `TODO: verify` whether additional role checks are required client-side.

## 9. Contribution Workflow (Suggested)
1. Branch from latest mainline.
2. Keep feature changes inside relevant `src/features/<domain>` module.
3. Avoid direct Axios usage in UI components; use shared API layer.
4. Add tests for new behavior (Vitest for frontend, JUnit for backend).
5. Run both test suites before PR.
