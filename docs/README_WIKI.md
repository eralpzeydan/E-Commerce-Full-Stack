# E-Commerce Full Stack Wiki

This wiki documents the repository at:
- `front-end/` (Next.js 16 + React 19 storefront/admin UI)
- `back-end/` (Spring Boot 3.5 Java 17 API)

## Quick Links
- [Architecture](./ARCHITECTURE.md)
- [API](./API.md)
- [Database](./DATABASE.md)
- [Frontend](./FRONTEND.md)
- [Backend](./BACKEND.md)
- [Deployment](./DEPLOYMENT.md)
- [Onboarding](./ONBOARDING.md)

## Repository Map
- `front-end/src/app`: route entrypoints and layouts
- `front-end/src/features`: domain modules (`auth`, `products`, `cart`, `checkout`, `orders`, `admin`, `categories`)
- `front-end/src/shared`: reusable API client, UI, config, and utilities
- `back-end/src/main/java/com/eralp/ecommerce`: API source code
- `back-end/src/main/resources/application.yaml`: runtime configuration
- `back-end/docker-compose.yml`: local containerized backend + PostgreSQL

## Current Status Summary
- Backend test suite passes (`./mvnw test`): 27 tests passed.
- Frontend test suite passes (`npm run test:run`): 17 tests passed.
- Product listing, product detail, auth forms, cart view, and protected orders access are wired.
- Admin screens and checkout UI are scaffolded but partially stubbed.

## Important Contract Notes
- `front-end/src/features/auth/hooks/use-auth-mutations.ts` stores token after register, but backend register currently returns `token: null` in `back-end/src/main/java/com/eralp/ecommerce/service/impl/AuthServiceImpl.java`.
  - `TODO: verify` expected register/login token behavior.
- `front-end/src/shared/api/endpoints.ts` defines `AUTH.ME`, but backend has no matching `/api/v1/auth/me` endpoint.
  - `TODO: verify` whether `/auth/me` is planned.
- `front-end/src/features/checkout/api/checkout-api.ts` sends checkout payload body, but backend checkout endpoint only uses `Idempotency-Key` header and authentication principal.
  - `TODO: verify` intended checkout request schema.
