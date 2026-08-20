package com.org.apitest.e2e;

import com.org.apitest.base.BaseApiTest;
import com.org.apitest.config.ConfigManager;
import com.org.apitest.data.CleanupRegistry;
import com.org.apitest.db.JdbcHelper;
import com.org.apitest.order.OrderClient;
import com.org.apitest.order.builder.OrderPayloadBuilder;
import com.org.apitest.payment.PaymentClient;
import com.org.apitest.payment.builder.PaymentPayloadBuilder;
import com.org.apitest.redis.RedisHelper;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;
import static java.util.concurrent.TimeUnit.SECONDS;

public class OrderPaymentE2ETest extends BaseApiTest {

    private OrderClient orderClient;
    private PaymentClient paymentClient;
    private JdbcHelper orderDb;
    private RedisHelper redis;

    @BeforeMethod(alwaysRun = true)
    public void initClients() {
        orderClient = new OrderClient();
        paymentClient = new PaymentClient();
        orderDb = new JdbcHelper(ConfigManager.get().databases().orderDb());
        redis = new RedisHelper(ConfigManager.get().redis());
    }

    @AfterMethod(alwaysRun = true)
    public void closeResources() {
        if (orderDb != null) {
            orderDb.close();
        }
        if (redis != null) {
            redis.close();
        }
    }

    @Test(groups = {"e2e", "order-payment-flow", "regression"})
    public void orderPaymentFlowUpdatesDbAndCache() {
        String orderId = orderClient.createOrder(OrderPayloadBuilder.defaultOrder())
                .then()
                .statusCode(201)
                .body("status", equalTo("CREATED"))
                .extract()
                .path("id");
        CleanupRegistry.register(() -> orderClient.cancelOrder(orderId));

        paymentClient.pay(orderId, PaymentPayloadBuilder.validPayment())
                .then()
                .statusCode(200);

        await().atMost(E2ETestConstants.DB_ASSERTION_TIMEOUT_SECONDS, SECONDS).untilAsserted(() -> {
            assertThat(orderDb.queryForSingleRow(
                    "SELECT status FROM orders WHERE id = ?", orderId))
                    .isPresent()
                    .get()
                    .extracting(row -> row.get("status"))
                    .isEqualTo("PAID");
        });

        await().atMost(E2ETestConstants.CACHE_ASSERTION_TIMEOUT_SECONDS, SECONDS).untilAsserted(() -> {
            String cachedStatus = redis.get("order:" + orderId + ":status");
            assertThat(cachedStatus).isEqualTo("PAID");
        });

        orderClient.getOrder(orderId).then().statusCode(200).body("status", equalTo("PAID"));
    }
}
