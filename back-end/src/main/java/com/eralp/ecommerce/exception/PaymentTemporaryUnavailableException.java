package com.eralp.ecommerce.exception;

public class PaymentTemporaryUnavailableException extends RuntimeException {

    public PaymentTemporaryUnavailableException(String message) {
        super(message);
    }
}
