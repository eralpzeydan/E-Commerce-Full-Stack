# Frontend Architecture Contract

## Purpose
This project uses a route-first + feature-first hybrid structure for production-grade maintainability.

## Ownership Boundaries
- `src/app`: route entry points, layouts, route composition, metadata, route-level redirects/guards.
- `src/features`: domain logic and feature-specific UI (`auth`, `products`, `cart`, `checkout`, `orders`, `admin`).
- `src/shared`: cross-cutting code only (infrastructure, reusable primitives, generic helpers).

## Non-Negotiable Rules
1. `page.tsx` files must stay thin.
2. Domain logic does not belong in `src/app`.
3. API calls are centralized in `shared/api/http-client.ts` + `features/*/api/*` wrappers.
4. Presentational components do not call HTTP clients directly.
5. `shared/` is not a dumping ground.

## Shared Placement Rule
Move code to `shared/` only when one of the following is true:
- The code is infrastructure (`api`, `config`, HTTP, tokens, env, common query utilities).
- The code is domain-agnostic and reused by at least two distinct features.

If the code mentions a specific domain concept (`Product`, `Order`, `Cart`, `Auth`), keep it in its feature module.

## Feature Template
Each feature should follow a predictable shape:
- `api/`
- `hooks/`
- `components/`
- `types/`
- Optional: `validation/`, `state/`, `utils/`

## UI State Contract
Every feature screen must explicitly handle:
- loading
- error
- empty
- success

## App Router Composition
- Root layout: global styles and providers.
- Route-group layouts: storefront shell (`(store)`), auth shell (`(auth)`), admin shell (`admin`).
- Route files only compose feature screens.

## Auth Strategy (Current)
- JWT token managed client-side for v1.
- Token injection in Axios request interceptor.
- 401 handling centralized in interceptor.
- Route-level guard via Next.js proxy.

## Interview Defense
- Framework-aligned routing via App Router.
- Feature modules enable parallel development and safer refactors.
- Thin route files reduce coupling.
- Shared code is intentionally constrained to avoid entropy.
