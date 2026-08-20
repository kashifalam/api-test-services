package com.org.apitest.order.builder;

import com.org.apitest.data.DataIsolationContext;
import com.org.apitest.order.model.OrderRequest;

/**
 * Factory methods for order test payloads.
 */
public final class OrderPayloadBuilder {

    public static final String DEFAULT_PRODUCT_ID = "PROD-001";
    public static final String DEFAULT_EMAIL_DOMAIN = "org.com";
    public static final int DEFAULT_QUANTITY = 1;

    private OrderPayloadBuilder() {
    }

    public static OrderRequest defaultOrder() {
        return new OrderRequest(
                DataIsolationContext.uniqueEmail(DEFAULT_EMAIL_DOMAIN),
                DEFAULT_PRODUCT_ID,
                DEFAULT_QUANTITY,
                DataIsolationContext.getTestRunId());
    }

    public static OrderRequest withProduct(String productId, int quantity) {
        OrderRequest base = defaultOrder();
        return new OrderRequest(base.customerEmail(), productId, quantity, base.correlationId());
    }
}
