package com.org.apitest.e2e;

import com.org.apitest.base.BaseApiTest;
import com.org.apitest.data.CleanupRegistry;
import com.org.apitest.order.OrderClient;
import com.org.apitest.order.builder.OrderPayloadBuilder;
import com.org.apitest.payment.PaymentClient;
import com.org.apitest.payment.builder.PaymentPayloadBuilder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.equalTo;

public class OrderPaymentE2ETest extends BaseApiTest {

    private OrderClient orderClient;
    private PaymentClient paymentClient;

    @BeforeMethod(alwaysRun = true)
    public void initClients() {
        orderClient = new OrderClient();
        paymentClient = new PaymentClient();
    }

    @Test(groups = {"e2e", "order-payment-flow", "regression"})
    public void orderPaymentFlowMarksOrderPaymentSuccess() {
        String orderId = orderClient.createOrder(OrderPayloadBuilder.defaultOrder())
                .then()
                .statusCode(201)
                .body("status", equalTo("CREATED"))
                .extract()
                .path("orderId");
        CleanupRegistry.register(() -> orderClient.cancelOrder(orderId));

        String paymentId = paymentClient.pay(PaymentPayloadBuilder.validPayment(orderId))
                .then()
                .statusCode(201)
                .body("orderId", equalTo(orderId))
                .body("status", equalTo("SUCCESS"))
                .extract()
                .path("paymentId");

        // The payment-service calls back into the order-service to advance the order.
        orderClient.getOrder(orderId)
                .then()
                .statusCode(200)
                .body("status", equalTo("PAYMENT_SUCCESS"));

        // The created payment object is retrievable by id and by order.
        paymentClient.getPayment(paymentId)
                .then()
                .statusCode(200)
                .body("paymentId", equalTo(paymentId))
                .body("orderId", equalTo(orderId))
                .body("paymentMethod", equalTo(PaymentPayloadBuilder.DEFAULT_PAYMENT_METHOD));

        paymentClient.getPaymentsForOrder(orderId)
                .then()
                .statusCode(200)
                .body("[0].orderId", equalTo(orderId))
                .body("[0].status", equalTo("SUCCESS"));
    }
}
