package com.org.apitest.payment.builder;

import com.org.apitest.payment.model.PaymentRequest;

/**
 * Factory methods for payment test payloads.
 */
public final class PaymentPayloadBuilder {

    public static final String DEFAULT_PAYMENT_METHOD = "CREDIT_CARD";
    public static final double DEFAULT_AMOUNT = 1299.99;

    private PaymentPayloadBuilder() {
    }

    public static PaymentRequest validPayment(String orderId) {
        return new PaymentRequest(orderId, DEFAULT_AMOUNT, DEFAULT_PAYMENT_METHOD);
    }
}
