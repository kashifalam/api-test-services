package com.org.apitest.order;

import com.org.apitest.order.model.OrderRequest;

public final class OrderPayloadBuilder {

    private String title = "Test Order";
    private String body = "Order body";
    private int userId = 1;

    private OrderPayloadBuilder() {}

    public static OrderPayloadBuilder defaultOrder() {
        return new OrderPayloadBuilder();
    }

    public OrderPayloadBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public OrderPayloadBuilder withBody(String body) {
        this.body = body;
        return this;
    }

    public OrderPayloadBuilder withUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public OrderPayloadBuilder withTestRunId(String testRunId) {
        this.title = "Order-" + testRunId;
        return this;
    }

    public OrderRequest build() {
        return new OrderRequest(title, body, userId);
    }
}
