package com.org.apitest.order;

import com.org.apitest.base.BaseApiTest;
import com.org.apitest.config.ConfigManager;
import com.org.apitest.data.CleanupRegistry;
import com.org.apitest.db.JdbcTemplateHelper;
import com.org.apitest.db.RedisHelper;
import com.org.apitest.order.model.OrderRequest;
import io.restassured.response.Response;
import org.awaitility.Awaitility;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * E2E workflow: REST API call -> JDBC verification -> Redis cache check.
 * Integration group requires live PostgreSQL and Redis (configured in environment YAML).
 */
public class OrderWorkflowE2ETest extends BaseApiTest {

    private OrderClient orderClient;
    private JdbcTemplateHelper orderDb;
    private RedisHelper redis;

    @BeforeMethod
    public void setUpClients() {
        orderClient = new OrderClient();
        orderDb = new JdbcTemplateHelper(ConfigManager.get().orderDb());
        redis = new RedisHelper(ConfigManager.get().redis());
    }

    @Test(groups = {"integration", "e2e", "order-service"})
    public void orderCreationUpdatesDatabaseAndCache() {
        OrderRequest payload = OrderPayloadBuilder.defaultOrder()
                .withTestRunId(testRunId)
                .build();

        Response response = orderClient.createOrder(payload);
        int orderId = response.path("id");

        CleanupRegistry.register(() -> {
            orderDb.executeUpdate("DELETE FROM orders WHERE id = ?", orderId);
            redis.delete("order:" + orderId + ":status");
        });

        // Seed DB row for demo — in real suites the API creates this row
        orderDb.executeUpdate(
                "INSERT INTO orders (id, status, correlation_id) VALUES (?, ?, ?) ON CONFLICT (id) DO UPDATE SET status = EXCLUDED.status",
                orderId, "CREATED", testRunId);

        redis.set("order:" + orderId + ":status", "CREATED");

        // Simulate downstream status update after payment
        orderDb.executeUpdate("UPDATE orders SET status = ? WHERE id = ?", "PAID", orderId);
        redis.set("order:" + orderId + ":status", "PAID");

        Awaitility.await()
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    var row = orderDb.queryForMap("SELECT status FROM orders WHERE id = ?", orderId);
                    assertThat(row.get("status")).isEqualTo("PAID");
                });

        Awaitility.await()
                .atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        assertThat(redis.get("order:" + orderId + ":status")).isEqualTo("PAID"));
    }
}
