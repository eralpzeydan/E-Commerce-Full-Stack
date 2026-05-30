# Database Documentation

## Engine and ORM
- Main DB: PostgreSQL (configured in `back-end/src/main/resources/application.yaml`)
- ORM: Spring Data JPA + Hibernate
- DDL mode: `spring.jpa.hibernate.ddl-auto=update`

Entity source path:
- `back-end/src/main/java/com/eralp/ecommerce/entity/*`

## Tables and Columns

### `users`
Entity: `User.java`
- `id` (PK, identity)
- `first_name` (varchar 100, not null)
- `last_name` (varchar 100, not null)
- `email` (varchar 225, unique, not null)
- `password` (varchar 255, nullable)
- `role` (enum string, not null; `ROLE_USER|ROLE_ADMIN`)

Constraints:
- Unique email via `uk_users_email`.

### `categories`
Entity: `Category.java`
- `id` (PK)
- `name` (varchar 100, unique, not null)
- `description` (varchar 500, nullable)

Constraints:
- Unique name via `uk_categories_name`.

### `products`
Entity: `Product.java`
- `id` (PK)
- `name` (varchar 150, not null)
- `description` (varchar 1000, nullable)
- `price` (decimal 19,2, not null)
- `stock` (integer, not null)
- `category_id` (FK -> categories.id, not null)

### `carts`
Entity: `Cart.java`
- `id` (PK)
- `user_id` (one-to-one FK -> users.id, unique, not null)

### `cart_items`
Entity: `CartItem.java`
- `id` (PK)
- `cart_id` (FK -> carts.id, not null)
- `product_id` (FK -> products.id, not null)
- `quantity` (integer, not null)
- `unit_price` (decimal 19,2, not null)

Constraints:
- Unique (`cart_id`, `product_id`) via `uk_cart_items_cart_product`.

### `orders`
Entity: `Order.java`
- `id` (PK)
- `created_at` (timestamp, not null; set in `@PrePersist`)
- `status` (enum string; `PENDING|CONFIRMED|CANCELLED`)
- `total_amount` (decimal 19,2, not null)
- `user_id` (FK -> users.id, not null)

### `order_items`
Entity: `OrderItem.java`
- `id` (PK)
- `quantity` (integer, not null)
- `unit_price` (decimal 19,2, not null)
- `line_total` (decimal 19,2, not null)
- `order_id` (FK -> orders.id, not null)
- `product_id` (FK -> products.id, not null)

### `idempotency_records`
Entity: `IdempotencyRecord.java`
- `id` (PK)
- `idempotency_key` (varchar 100, unique, not null)
- `user_id` (long, not null)
- `operation_type` (`CHECKOUT`, not null)
- `request_hash` (varchar 64, nullable)
- `status` (`PROCESSING|SUCCESS|FAILED`, not null)
- `response_order_id` (long, nullable)
- `created_at` (timestamp, not null)
- `updated_at` (timestamp, not null)

Constraints:
- Unique idempotency key via `uk_idempotency_key`.

## Relationships
- `users` 1:1 `carts`
- `carts` 1:N `cart_items`
- `categories` 1:N `products`
- `users` 1:N `orders`
- `orders` 1:N `order_items`
- `products` 1:N `order_items`

## Seed Data
Initialization script:
- `back-end/src/main/resources/data.sql`

Seeds include:
- Categories: `Electronics`, `Snacks`
- Products: `MacBook Air`, `iPhone 15`, `AirPods Pro`, `Almonds`, `Cashews`, `Dark Chocolate`

`spring.sql.init.mode=always` is set in `application.yaml`.

## Data Lifecycle Notes
- Cart is auto-created lazily when accessed by user.
- Cart checkout creates immutable order snapshot (`order_items`) using captured unit prices.
- Cart items are deleted after successful payment authorization.
- Idempotency table protects duplicate order creation per key.

## Items to Verify
- Checkout does not currently decrement `products.stock`.
  - `TODO: verify` inventory strategy.
- `idempotency_records.user_id` is scalar (not FK relation in entity mapping).
  - `TODO: verify` if FK constraint is desired.
- Current DDL strategy is `update`; production migrations (Flyway/Liquibase) are not present.
  - `TODO: verify` migration plan.
