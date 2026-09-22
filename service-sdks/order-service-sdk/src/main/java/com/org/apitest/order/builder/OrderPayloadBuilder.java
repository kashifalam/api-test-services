package com.org.apitest.order.builder;

import com.org.apitest.data.DataIsolationContext;
import com.org.apitest.order.model.OrderRequest;

/**
 * Factory methods for order test payloads.
 */
public final class OrderPayloadBuilder {

    public static final String DEFAULT_ITEM = "Laptop";
    public static final int DEFAULT_QUANTITY = 1;
    public static final double DEFAULT_AMOUNT = 1299.99;

    private OrderPayloadBuilder() {
    }

    public static OrderRequest defaultOrder() {
        return new OrderRequest(
                "user-" + DataIsolationContext.getTestRunId(),
                DEFAULT_ITEM,
                DEFAULT_QUANTITY,
                DEFAULT_AMOUNT);
    }

    public static OrderRequest withQuantity(int quantity) {
        OrderRequest base = defaultOrder();
        return new OrderRequest(base.userId(), base.item(), quantity, base.amount());
    }
}
