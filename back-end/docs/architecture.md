# Architecture Overview

## System Context
This backend serves an e-commerce domain with authentication, catalog management, cart operations, and checkout. It exposes REST APIs to clients, persists operational data in PostgreSQL, accelerates selected read paths with Redis caching, and publishes domain events through RabbitMQ.

## High-Level Architecture
The system uses a layered architecture:

- Controllers: HTTP entry points and request/response mapping
- Services: business rules, orchestration, and transaction boundaries
- Repositories: persistence operations via Spring Data JPA
- Security: JWT filter chain and role-based access checks
- Integrations: payment gateway abstraction, Redis cache manager, RabbitMQ producer/consumer

## Main Modules
- Auth: registration, login, password hashing, JWT generation
- User: user CRUD operations
- Product: product CRUD, filtering/pagination/sorting, cache-aware reads
- Category: category CRUD
- Cart: add/update/remove item operations and cart total aggregation
- Order: checkout flow, idempotency enforcement, order persistence
- Idempotency: key ownership, request hash checks, processing/success/failure state management
- Messaging: order-created event publishing and consuming
- Observability: health endpoints, actuator info, request logging filter

## Request Lifecycle
1. Request enters through `SecurityFilterChain`.
2. Public routes pass directly; protected routes require JWT validation.
3. Controller delegates business behavior to service layer.
4. Service executes transaction-scoped logic and repository operations.
5. For cacheable read paths, cache is checked before querying database.
6. For checkout, payment and idempotency logic are executed within domain workflow.
7. Response is mapped to DTO and returned.

## Authentication Flow
1. `POST /api/v1/auth/register` creates a new user with hashed password.
2. `POST /api/v1/auth/login` validates credentials and returns JWT.
3. JWT is sent as bearer token to protected routes.
4. `JwtAuthenticationFilter` resolves user identity and authorities.
5. Authorization rules in `SecurityConfig` enforce role-based permissions.

## Cart and Checkout Flow
1. User adds products to cart using `POST /api/v1/cart/items`.
2. Cart service creates cart on demand and updates quantities.
3. Checkout starts via `POST /api/v1/orders/checkout` with `Idempotency-Key`.
4. Idempotency service validates key ownership and request consistency.
5. Order service validates non-empty cart, creates order items, and totals.
6. Payment client authorizes payment with retry for transient failures.
7. On success, cart items are cleared.
8. Order-created event is published after transaction commit.

## Caching Strategy
- Cache provider: Redis (`spring.cache.type=redis`)
- Primary cache targets: product list and product detail reads
- Cache names: `products`, `product`
- TTL: 10 minutes
- Invalidation: product create/update/delete evicts related entries
- Test and docker profile note: cache integration may be simplified/disabled depending on profile

## Messaging Strategy
- Broker: RabbitMQ
- Exchange: `order.exchange`
- Queue: `order.created.queue`
- Routing key: `order.created`
- Producer publishes `OrderCreatedEvent` after transaction commit
- Consumer logs event and simulates downstream action (confirmation flow)

## Testing Strategy
- Unit/service tests for domain logic
- Integration tests for auth, security, checkout idempotency, and payment retry behavior
- Test profile uses H2 with disabled external integrations for stable and fast execution

## Design Decisions
- Layered architecture for maintainability and separation of concerns
- JWT stateless auth to keep API horizontally scalable
- Idempotency records to protect checkout from duplicate order creation
- Retry wrapper around payment gateway for transient error resilience
- Post-commit event publication to avoid emitting events for rolled back transactions

## Known Trade-offs
- H2 is fast for tests but not fully equivalent to PostgreSQL behavior
- Current consumer behavior is minimal and does not include dead-letter handling yet
- Cache scope is intentionally narrow to keep invalidation complexity low
- Logging/metrics are present at baseline level; production observability can be deepened

