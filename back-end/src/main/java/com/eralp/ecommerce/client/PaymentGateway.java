package com.eralp.ecommerce.client;

import java.math.BigDecimal;

public interface PaymentGateway {

    void authorize(Long orderId, BigDecimal amount, String idempotencyKey);
}
