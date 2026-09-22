package com.org.apitest.payment;

import com.org.apitest.config.ServiceKey;
import com.org.apitest.http.HttpClientFacade;
import com.org.apitest.observability.AllureSteps;
import com.org.apitest.payment.model.PaymentRequest;
import io.qameta.allure.Step;
import io.restassured.response.Response;

/**
 * REST client for the Payment service API.
 */
public final class PaymentClient {

    private final HttpClientFacade http;

    public PaymentClient() {
        this(HttpClientFacade.forService(ServiceKey.PAYMENT));
    }

    PaymentClient(HttpClientFacade http) {
        this.http = http;
    }

    @Step("Submit payment for order: {payload.orderId}")
    public Response pay(PaymentRequest payload) {
        Response response = http.post("/payments", payload);
        AllureSteps.attachJson("payment-response", response.asPrettyString());
        return response;
    }

    @Step("Get payment by id: {paymentId}")
    public Response getPayment(String paymentId) {
        Response response = http.get("/payments/" + paymentId);
        AllureSteps.attachJson("get-payment-response", response.asPrettyString());
        return response;
    }

    @Step("Get payments for order: {orderId}")
    public Response getPaymentsForOrder(String orderId) {
        Response response = http.get("/payments/order/" + orderId);
        AllureSteps.attachJson("get-payments-by-order-response", response.asPrettyString());
        return response;
    }
}
