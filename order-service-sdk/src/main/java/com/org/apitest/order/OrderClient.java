package com.org.apitest.order;

import com.org.apitest.config.ConfigManager;
import com.org.apitest.http.AuthTokenProvider;
import com.org.apitest.http.HttpClientFacade;
import com.org.apitest.observability.AllureSteps;
import com.org.apitest.order.model.OrderRequest;
import io.restassured.response.Response;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Order service API client.
 * Uses jsonplaceholder.typicode.com /posts endpoints as a stand-in for order CRUD.
 */
public class OrderClient {

    private final HttpClientFacade http;

    public OrderClient() {
        var serviceConfig = ConfigManager.get().orderService();
        this.http = new HttpClientFacade(serviceConfig.baseUrl(), new AuthTokenProvider());
    }

    public Response createOrder(OrderRequest payload) {
        return AllureSteps.step("Create order: " + payload.title(), () ->
                http.post("/posts", payload)
                        .statusCode(201)
                        .body("id", notNullValue())
                        .extract()
                        .response());
    }

    public Response getOrder(int orderId) {
        return AllureSteps.step("Get order by id: " + orderId, () ->
                http.get("/posts/" + orderId)
                        .statusCode(200)
                        .body("id", equalTo(orderId))
                        .extract()
                        .response());
    }

    public void cancelOrder(int orderId) {
        AllureSteps.step("Cancel (delete) order: " + orderId, () ->
                http.delete("/posts/" + orderId)
                        .statusCode(200));
    }
}
