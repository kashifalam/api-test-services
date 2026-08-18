package com.org.apitest.payment;

import java.util.HashMap;
import java.util.Map;

public final class PaymentPayloadBuilder {

    private PaymentPayloadBuilder() {
    }

    public static Map<String, Object> validPayment() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("method", "CARD");
        payload.put("amount", 99.99);
        payload.put("currency", "USD");
        return payload;
    }
}
