TRUNCATE TABLE
    idempotency_records,
    order_items,
    orders,
    cart_items,
    carts,
    products,
    categories,
    users
RESTART IDENTITY CASCADE;
