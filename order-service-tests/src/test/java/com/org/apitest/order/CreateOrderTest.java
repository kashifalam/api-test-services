package com.org.apitest.order;

import com.org.apitest.base.BaseApiTest;
import com.org.apitest.data.CleanupRegistry;
import com.org.apitest.order.model.OrderRequest;
import io.restassured.response.Response;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateOrderTest extends BaseApiTest {

    private OrderClient orderClient;

    @BeforeMethod
    public void setUpClient() {
        orderClient = new OrderClient();
    }

    @Test(groups = {"smoke", "order-service", "regression"})
    public void shouldCreateOrderWithValidPayload() {
        OrderRequest payload = OrderPayloadBuilder.defaultOrder()
                .withTestRunId(testRunId)
                .build();

        Response response = orderClient.createOrder(payload);
        int orderId = response.jsonPath().getInt("id");

        CleanupRegistry.register(() -> orderClient.cancelOrder(orderId));

        assertThat(orderId).isPositive();
        assertThat(response.jsonPath().getString("title")).isEqualTo(payload.title());
    }

    @Test(groups = {"smoke", "order-service"})
    public void shouldGetExistingOrder() {
        Response response = orderClient.getOrder(1);

        assertThat(response.jsonPath().getInt("id")).isEqualTo(1);
        assertThat(response.jsonPath().getInt("userId")).isPositive();
    }
}
