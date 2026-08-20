package com.org.apitest.payment.model;

/**
 * Payment submission request payload.
 */
public record PaymentRequest(
        String method,
        double amount,
        String currency) {
}
