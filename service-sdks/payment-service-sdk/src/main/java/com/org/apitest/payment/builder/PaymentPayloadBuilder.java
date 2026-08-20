package com.org.apitest.payment.builder;

import com.org.apitest.payment.model.PaymentRequest;

/**
 * Factory methods for payment test payloads.
 */
public final class PaymentPayloadBuilder {

    public static final String DEFAULT_METHOD = "CARD";
    public static final double DEFAULT_AMOUNT = 99.99;
    public static final String DEFAULT_CURRENCY = "USD";

    private PaymentPayloadBuilder() {
    }

    public static PaymentRequest validPayment() {
        return new PaymentRequest(DEFAULT_METHOD, DEFAULT_AMOUNT, DEFAULT_CURRENCY);
    }
}
