package com.org.apitest.order;

import com.org.apitest.base.BaseApiTest;
import com.org.apitest.data.CleanupRegistry;
import com.org.apitest.order.builder.OrderPayloadBuilder;
import com.org.apitest.order.model.OrderRequest;
import io.restassured.response.Response;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class CreateOrderTest extends BaseApiTest {

    private OrderClient orderClient;

    @BeforeMethod(alwaysRun = true)
    public void initClient() {
        orderClient = new OrderClient();
    }

    @Test(groups = {"smoke", "order-service", "regression"})
    public void shouldCreateOrderWithValidPayload() {
        OrderRequest payload = OrderPayloadBuilder.defaultOrder();

        Response response = orderClient.createOrder(payload);
        response.then().statusCode(201).body("status", equalTo("CREATED"));

        String orderId = response.path("orderId");
        assertThat(orderId).isNotBlank();

        CleanupRegistry.register(() -> orderClient.cancelOrder(orderId));

        orderClient.getOrder(orderId).then().statusCode(200).body("status", equalTo("CREATED"));
    }

    @Test(groups = {"regression", "order-service"})
    public void shouldRejectOrderWithInvalidQuantity() {
        OrderRequest payload = OrderPayloadBuilder.withQuantity(-1);

        orderClient.createOrder(payload).then().statusCode(400);
    }
}
