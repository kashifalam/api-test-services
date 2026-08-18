package com.org.apitest.order.builder;

import com.org.apitest.data.DataIsolationContext;
import com.org.apitest.order.model.OrderRequest;

public final class OrderPayloadBuilder {

    private OrderPayloadBuilder() {
    }

    public static OrderRequest defaultOrder() {
        return new OrderRequest()
                .setProductId("PROD-001")
                .setQuantity(1)
                .setCustomerEmail(DataIsolationContext.uniqueEmail("org.com"))
                .setCorrelationId(DataIsolationContext.getTestRunId());
    }

    public static OrderRequest withProduct(String productId, int quantity) {
        return defaultOrder()
                .setProductId(productId)
                .setQuantity(quantity);
    }
}
