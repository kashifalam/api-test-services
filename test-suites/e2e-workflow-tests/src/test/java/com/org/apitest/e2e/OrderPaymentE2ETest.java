package com.org.apitest.e2e;

import com.org.apitest.base.BaseApiTest;
import com.org.apitest.config.ConfigManager;
import com.org.apitest.data.CleanupRegistry;
import com.org.apitest.db.JdbcHelper;
import com.org.apitest.order.OrderClient;
import com.org.apitest.order.builder.OrderPayloadBuilder;
import com.org.apitest.payment.PaymentClient;
import com.org.apitest.payment.PaymentPayloadBuilder;
import com.org.apitest.redis.RedisHelper;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;
import static java.util.concurrent.TimeUnit.SECONDS;

public class OrderPaymentE2ETest extends BaseApiTest {

    private OrderClient orderClient;
    private PaymentClient paymentClient;
    private JdbcHelper orderDb;
    private RedisHelper redis;

    @BeforeClass(alwaysRun = true)
    public void initClients() {
        orderClient = new OrderClient();
        paymentClient = new PaymentClient();
        orderDb = new JdbcHelper(ConfigManager.get().getDatabases().getOrderDb());
        redis = new RedisHelper(ConfigManager.get().getRedis());
    }

    @AfterClass(alwaysRun = true)
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
        String orderId = orderClient.createOrderAndExtractId(OrderPayloadBuilder.defaultOrder());
        CleanupRegistry.register(() -> orderClient.cancelOrder(orderId));

        paymentClient.pay(orderId, PaymentPayloadBuilder.validPayment())
                .then()
                .statusCode(200);

        await().atMost(15, SECONDS).untilAsserted(() -> {
            Map<String, Object> row = orderDb.queryForMap(
                    "SELECT status FROM orders WHERE id = ?", orderId);
            assertThat(row).isNotEmpty();
            assertThat(row.get("status")).isEqualTo("PAID");
        });

        await().atMost(5, SECONDS).untilAsserted(() -> {
            String cachedStatus = redis.get("order:" + orderId + ":status");
            assertThat(cachedStatus).isEqualTo("PAID");
        });

        orderClient.getOrder(orderId).then().statusCode(200).body("status", equalTo("PAID"));
    }
}
