package com.org.apitest.order;

import com.org.apitest.config.ServiceKey;
import com.org.apitest.http.HttpClientFacade;
import com.org.apitest.observability.AllureSteps;
import com.org.apitest.order.model.OrderRequest;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.Map;

/**
 * REST client for the Order service API.
 */
public final class OrderClient {

    private final HttpClientFacade http;

    public OrderClient() {
        this(HttpClientFacade.forService(ServiceKey.ORDER));
    }

    OrderClient(HttpClientFacade http) {
        this.http = http;
    }

    @Step("Create order for product {payload.productId}")
    public Response createOrder(OrderRequest payload) {
        Response response = http.post("/orders", payload);
        AllureSteps.attachJson("create-order-response", response.asPrettyString());
        return response;
    }

    @Step("Get order by id: {orderId}")
    public Response getOrder(String orderId) {
        Response response = http.get("/orders/" + orderId);
        AllureSteps.attachJson("get-order-response", response.asPrettyString());
        return response;
    }

    @Step("Update status of order {orderId} to {status}")
    public Response updateStatus(String orderId, String status) {
        Response response = http.put("/orders/" + orderId + "/status", Map.of("status", status));
        AllureSteps.attachJson("update-order-status-response", response.asPrettyString());
        return response;
    }

    @Step("Cancel order: {orderId}")
    public Response cancelOrder(String orderId) {
        return updateStatus(orderId, "CANCELLED");
    }
}
