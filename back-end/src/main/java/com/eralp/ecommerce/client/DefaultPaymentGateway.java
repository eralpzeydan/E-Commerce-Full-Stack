package com.eralp.ecommerce.client;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DefaultPaymentGateway implements PaymentGateway {

    @Override
    public void authorize(Long orderId, BigDecimal amount, String idempotencyKey) {
        if (orderId == null || amount == null) {
            throw new IllegalArgumentException("Payment request contains invalid fields");
        }
    }
}
