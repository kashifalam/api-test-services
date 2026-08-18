package com.org.apitest.order;

import com.org.apitest.http.HttpClientFacade;
import com.org.apitest.observability.AllureSteps;
import com.org.apitest.order.model.OrderRequest;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static org.hamcrest.Matchers.equalTo;

public class OrderClient {

    private final HttpClientFacade http;

    public OrderClient() {
        this.http = HttpClientFacade.forService("order");
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

    @Step("Cancel order: {orderId}")
    public Response cancelOrder(String orderId) {
        return http.delete("/orders/" + orderId);
    }

    public String createOrderAndExtractId(OrderRequest payload) {
        return createOrder(payload)
                .then()
                .statusCode(201)
                .body("status", equalTo("CREATED"))
                .extract()
                .path("id");
    }
}
