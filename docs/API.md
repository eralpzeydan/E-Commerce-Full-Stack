# API Documentation

Base URL (default local):
- `http://localhost:8080`

Primary backend routing:
- Controllers in `back-end/src/main/java/com/eralp/ecommerce/controller/*`

## Authentication Model
- JWT bearer token required for protected endpoints.
- Send as `Authorization: Bearer <token>`.
- `Idempotency-Key` header is required for checkout.

JWT generation/validation:
- `back-end/src/main/java/com/eralp/ecommerce/security/JwtService.java`
- `back-end/src/main/java/com/eralp/ecommerce/security/JwtAuthenticationFilter.java`

## Endpoint Catalog

### Health
- `GET /`
- `GET /api/v1/health`
- `GET /actuator/health`
- `GET /actuator/info`

Controller: `back-end/src/main/java/com/eralp/ecommerce/controller/HealthCheckController.java`

### Auth
Controller: `back-end/src/main/java/com/eralp/ecommerce/controller/AuthController.java`

- `POST /api/auth/register`
- `POST /api/v1/auth/register`
  - Body (`RegisterRequestDto`):
    - `name` (required, max 200)
    - `email` (required, valid email, max 225)
    - `password` (required, min 6)
  - Response (`AuthResponseDto`): `{ userId, name, email, token, message }`

- `POST /api/auth/login`
- `POST /api/v1/auth/login`
  - Body (`LoginRequestDto`):
    - `email` (required, valid email)
    - `password` (required)
  - Response (`AuthResponseDto`): `{ userId, name, email, token, message }`

Notes:
- Register currently returns `token: null` in service implementation.
  - `TODO: verify` if register should auto-login.

### Products
Controller: `back-end/src/main/java/com/eralp/ecommerce/controller/ProductController.java`

- `GET /api/v1/products`
  - Query params:
    - `page` default `0`
    - `size` default `10` (service validates 1..100)
    - `sortBy` default `id` (`id|name|price|stock`)
    - `sortDir` default `asc` (`asc|desc`)
    - `name` optional
    - `categoryId` optional
  - Response: paged `ProductResponse`

- `GET /api/v1/products/{id}`
  - Response: `ProductResponse`

- `POST /api/v1/products` (ADMIN)
  - Body (`CreateProductRequest`): `name`, `description`, `price`, `stock`, `categoryId`

- `PUT /api/v1/products/{id}` (ADMIN)
  - Body (`UpdateProductRequest`): same fields as create

- `DELETE /api/v1/products/{id}` (ADMIN)

### Categories
Controller: `back-end/src/main/java/com/eralp/ecommerce/controller/CategoryController.java`

- `GET /api/v1/categories`
- `GET /api/v1/categories/{id}`
- `POST /api/v1/categories` (ADMIN)
- `PUT /api/v1/categories/{id}` (ADMIN)
- `DELETE /api/v1/categories/{id}` (ADMIN)

### Cart
Controller: `back-end/src/main/java/com/eralp/ecommerce/controller/CartController.java`

Protected (USER or ADMIN):
- `GET /api/v1/cart`
- `POST /api/v1/cart/items`
  - Body: `{ productId, quantity>=1 }`
- `PUT /api/v1/cart/items/{cartItemId}`
  - Body: `{ quantity>=0 }` (`0` deletes the item)
- `DELETE /api/v1/cart/items/{cartItemId}`

### Orders
Controller: `back-end/src/main/java/com/eralp/ecommerce/controller/OrderController.java`

Protected (USER or ADMIN):
- `POST /api/v1/orders/checkout`
  - Headers:
    - `Idempotency-Key` required
  - Response: `OrderResponse`
- `GET /api/v1/orders/secure-test`
  - Response text: authenticated check

Notes:
- Checkout endpoint does not declare `@RequestBody`.
  - Frontend currently sends a checkout payload object anyway.
  - `TODO: verify` intended request body contract.

### Admin
Controller: `back-end/src/main/java/com/eralp/ecommerce/controller/AdminController.java`

- `GET /api/admin/dashboard` (ADMIN)

### Users
Controller: `back-end/src/main/java/com/eralp/ecommerce/controller/UserController.java`

- `POST /api/v1/users`
- `GET /api/v1/users`
- `GET /api/v1/users/{id}`
- `DELETE /api/v1/users/{id}`

Security note:
- These are not explicitly role-scoped in `SecurityConfig`; they fall under authenticated access.
- `TODO: verify` if user management should be admin-only.

## Error Model
Global exception mapping:
- `back-end/src/main/java/com/eralp/ecommerce/exception/GlobalExceptionHandler.java`

Standard response (`ErrorResponse`):
- `timestamp`
- `status`
- `error`
- `message`
- `path`
- `validationErrors` (for request validation failures)

Common statuses:
- `400` bad input/validation
- `401` unauthorized
- `404` not found
- `409` conflict/idempotency/resource state conflicts
- `503` payment temporary unavailability
- `500` unhandled server errors

## Frontend API Client Mapping
- Shared client: `front-end/src/shared/api/http-client.ts`
- Interceptors: `front-end/src/shared/api/interceptors.ts`
- Endpoint constants: `front-end/src/shared/api/endpoints.ts`
- Feature wrappers:
  - `front-end/src/features/auth/api/auth-api.ts`
  - `front-end/src/features/products/api/products-api.ts`
  - `front-end/src/features/products/api/categories-api.ts`
  - `front-end/src/features/cart/api/cart-api.ts`
  - `front-end/src/features/checkout/api/checkout-api.ts`
  - `front-end/src/features/orders/api/orders-api.ts`

Contract mismatch notes:
- `AUTH.ME` endpoint exists in frontend constants but is not implemented backend-side.
  - `TODO: verify`
