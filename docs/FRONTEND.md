# Frontend Documentation

## Stack
Defined in `front-end/package.json`:
- Next.js `16.2.4` (App Router)
- React `19.2.4`
- TypeScript strict mode
- Tailwind CSS v4
- TanStack Query v5
- Axios
- React Hook Form + Zod
- Zustand
- Vitest + Testing Library

## Structure
Primary directories:
- `front-end/src/app`: route files and layout composition
- `front-end/src/features`: feature modules
- `front-end/src/shared`: reusable infrastructure and UI
- `front-end/src/styles`: design tokens (`tokens.css`)

### App Routes
Important route files:
- Store layout: `front-end/src/app/(store)/layout.tsx`
- Home: `front-end/src/app/(store)/page.tsx`
- Products list/detail:
  - `front-end/src/app/(store)/products/page.tsx`
  - `front-end/src/app/(store)/products/[id]/page.tsx`
- Cart/Checkout/Orders:
  - `front-end/src/app/(store)/cart/page.tsx`
  - `front-end/src/app/(store)/checkout/page.tsx`
  - `front-end/src/app/(store)/orders/page.tsx`
- Auth routes:
  - `front-end/src/app/(auth)/login/page.tsx`
  - `front-end/src/app/(auth)/register/page.tsx`
- Admin routes:
  - `front-end/src/app/admin/page.tsx`
  - `front-end/src/app/admin/products/page.tsx`
  - `front-end/src/app/admin/categories/page.tsx`
  - `front-end/src/app/admin/orders/page.tsx`

## Shared API Layer
Core files:
- `front-end/src/shared/config/env.ts` (env validation)
- `front-end/src/shared/api/http-client.ts` (Axios instance)
- `front-end/src/shared/api/interceptors.ts` (token injection + 401 clear)
- `front-end/src/shared/api/request.ts` (request helper + error normalization)
- `front-end/src/shared/api/endpoints.ts` (endpoint constants)

Provider wiring:
- `front-end/src/app/providers.tsx` creates QueryClient and sets interceptors once.

## Feature Modules

### `features/auth`
- API: `features/auth/api/auth-api.ts`
- Mutations: `features/auth/hooks/use-auth-mutations.ts`
- Forms:
  - `features/auth/components/login-form.tsx`
  - `features/auth/components/register-form.tsx`
- Validation:
  - `features/auth/validation/login-schema.ts`
  - `features/auth/validation/register-schema.ts`

Note:
- Register mutation stores token, but backend register currently returns `token: null`.
  - `TODO: verify`

### `features/products`
- List/detail APIs: `features/products/api/products-api.ts`
- Categories API: `features/products/api/categories-api.ts`
- Filter state via URL params: `features/products/hooks/use-product-filters.ts`
- Query mapping: `features/products/utils/product-query-params.ts`
- Main screen: `features/products/components/products-screen.tsx`

Current capabilities:
- Backend-driven pagination/sorting/filtering
- Loading, error, empty, success states
- Product detail screen

### `features/cart`
- API: `features/cart/api/cart-api.ts`
- Screen: `features/cart/components/cart-screen.tsx`
- Query hook: `features/cart/hooks/use-cart-query.ts`
- Zustand store: `features/cart/state/cart-store.ts`

Current capabilities:
- Read cart and render items/total.
- No UI actions yet for add/update/remove from cart screen.
  - `TODO: verify` implementation priority.

### `features/checkout`
- API: `features/checkout/api/checkout-api.ts`
- Hook: `features/checkout/hooks/use-checkout-mutation.ts`
- UI: `features/checkout/components/checkout-screen.tsx` (placeholder)

Notes:
- Checkout UI is scaffold-only.
- API sends `{ addressLine1, city, zipCode }` while backend does not consume body.
  - `TODO: verify` final contract.

### `features/orders`
- API: `features/orders/api/orders-api.ts`
- Screen: `features/orders/components/orders-screen.tsx`

Current behavior:
- Calls `/api/v1/orders/secure-test` to confirm authenticated access.

### `features/admin` and `features/categories`
- Admin pages are currently placeholder screens:
  - `features/admin/components/*`
  - `features/categories/components/admin-categories-screen.tsx`

## Layout and UI System
Storefront shell components:
- `front-end/src/shared/components/layout/storefront-shell.tsx`
- `store-top-bar.tsx`, `store-header.tsx`, `store-nav.tsx`, `store-category-nav.tsx`

UI primitives:
- `front-end/src/shared/components/ui/button.tsx`
- `card.tsx`, `input.tsx`, `badge.tsx`

Design tokens:
- `front-end/src/styles/tokens.css`

## Route Protection
- `front-end/src/proxy.ts` protects `/admin/*` using `auth_token` cookie presence.
- Token storage helper: `front-end/src/shared/utils/auth-token.ts`

Note:
- Guard checks only token existence, not token validity/role in frontend.
  - `TODO: verify` if stronger client-side checks are needed.

## Testing
Config and setup:
- `front-end/vitest.config.ts`
- `front-end/src/test/setup.tsx`

Current test coverage includes:
- Route thinness (`app/(store)/products/__tests__/page.test.tsx`)
- Storefront shell composition (`shared/components/layout/__tests__/storefront-shell.test.tsx`)
- Product API wrapper and product UI states (`features/products/**/__tests__`)

Latest local run:
- `npm run test:run` -> 6 files, 17 tests passed.
