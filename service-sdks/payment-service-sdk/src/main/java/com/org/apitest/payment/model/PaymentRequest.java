package com.org.apitest.payment.model;

/**
 * Payment submission request payload. Matches the payment-service {@code CreatePaymentRequest} contract.
 */
public record PaymentRequest(
        String orderId,
        double amount,
        String paymentMethod) {
}
