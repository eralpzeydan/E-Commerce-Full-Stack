# Backend Documentation

## Stack
Defined in `back-end/pom.xml`:
- Java 17
- Spring Boot 3.5.12
- Spring Web
- Spring Security
- Spring Data JPA
- Validation
- PostgreSQL driver
- Spring Cache + Redis
- Spring AMQP + RabbitMQ
- Spring Retry
- Actuator
- JWT (`io.jsonwebtoken`)

## Package Structure
Root package:
- `back-end/src/main/java/com/eralp/ecommerce`

Major modules:
- `config/`: security, redis, rabbitmq, retry, password
- `controller/`: REST endpoints
- `service/` + `service/impl/`: business logic
- `repository/`: JPA repositories
- `entity/`: persistence model
- `security/`: JWT logic
- `client/`: payment abstraction and retry wrapper
- `messaging/`: event producer/consumer
- `exception/`: domain exceptions + global handler
- `logging/`: request logging filter

## Runtime Configuration
Primary config files:
- `back-end/src/main/resources/application.yaml`
- `back-end/src/main/resources/application-docker.yml`
- `back-end/src/test/resources/application-test.yml`

Key defaults:
- Server port `8080`
- PostgreSQL datasource (env-overridable)
- Redis cache enabled in default profile
- RabbitMQ configured in default profile
- Actuator health/info exposed
- JWT secret/expiration from config

## Security and Access Control
Files:
- `config/SecurityConfig.java`
- `security/JwtAuthenticationFilter.java`
- `security/JwtService.java`
- `service/impl/CustomUserDetailsService.java`

Highlights:
- Stateless sessions + CSRF disabled.
- Public auth + read catalog endpoints.
- Admin-restricted write catalog and `/api/admin/**`.
- User/Admin access for cart/orders.
- JWT resolved from `Authorization` header.

## Domain Services

### Auth Service
- File: `service/impl/AuthServiceImpl.java`
- Handles register/login, email normalization, password hash, JWT generation.

### Product Service
- File: `service/impl/ProductServiceImpl.java`
- Supports pagination/sorting/filtering + Redis cache (`product`, `products`).
- Evicts caches on create/update/delete.

### Category Service
- File: `service/impl/CategoryServiceImpl.java`
- Enforces unique names, prevents delete while products exist.

### Cart Service
- File: `service/impl/CartServiceImpl.java`
- Lazy cart creation, add/update/remove items, total computation.

### Order Service
- File: `service/impl/OrderServiceImpl.java`
- Idempotent checkout with request hashing.
- Payment authorization via `PaymentClient`.
- Clears cart and publishes post-commit order event.

### Idempotency Service
- File: `service/IdempotencyService.java`
- Isolates PROCESSING/SUCCESS/FAILED record transitions in `REQUIRES_NEW` transactions.

## Integrations

### Payment Retry
- `client/PaymentClient.java`
- Retries `TransientPaymentException` up to 3 attempts with exponential backoff.
- Throws `PaymentTemporaryUnavailableException` on exhaustion.

### Redis Caching
- `config/RedisConfig.java`
- Cache TTL: 10 minutes for product caches.

### RabbitMQ Messaging
- `config/RabbitMqConfig.java`
  - Exchange: `order.exchange`
  - Queue: `order.created.queue`
  - Routing key: `order.created`
- Producer: `messaging/OrderEventProducer.java`
- Consumer: `messaging/OrderCreatedConsumer.java`

## Error Handling and Observability
- Global exception mapping: `exception/GlobalExceptionHandler.java`
- Request logs: `logging/RequestLoggingFilter.java`
- Health endpoints:
  - `/`
  - `/api/v1/health`
  - `/actuator/health`

## Test Coverage
Test source:
- `back-end/src/test/java/com/eralp/ecommerce`
- `back-end/src/test/java/com/eralp/ecommerce/integration`
- `back-end/src/test/java/com/eralp/ecommerce/service`

Coverage areas:
- Auth integration + unit tests
- Security integration tests
- Order integration tests
- Idempotency concurrency/replay tests
- Payment retry behavior tests

Latest local run:
- `./mvnw test` -> 27 tests passed.

## Backend Risks / TODOs
- Register returns null token while frontend expects to persist token on register.
  - `TODO: verify`
- `UserRepository` includes `Long id(long id);` declaration that is not used by service code.
  - `TODO: verify` whether accidental leftover.
- Checkout creates orders but does not reduce product stock.
  - `TODO: verify`
- `OrderStatus` remains `PENDING` in current checkout flow.
  - `TODO: verify` when status transitions should occur.
