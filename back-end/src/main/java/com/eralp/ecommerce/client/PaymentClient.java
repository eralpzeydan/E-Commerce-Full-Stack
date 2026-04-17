package com.eralp.ecommerce.client;

import com.eralp.ecommerce.exception.PaymentTemporaryUnavailableException;
import com.eralp.ecommerce.exception.TransientPaymentException;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentClient {

    private final PaymentGateway paymentGateway;

    @Retryable(
            retryFor = TransientPaymentException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 300, multiplier = 2)
    )
    public void authorizePayment(Long orderId, BigDecimal amount, String idempotencyKey) {
        paymentGateway.authorize(orderId, amount, idempotencyKey);
    }

    @Recover
    public void recover(TransientPaymentException ex, Long orderId, BigDecimal amount, String idempotencyKey) {
        throw new PaymentTemporaryUnavailableException(
                "Payment service is temporarily unavailable for order " + orderId
        );
    }
}
