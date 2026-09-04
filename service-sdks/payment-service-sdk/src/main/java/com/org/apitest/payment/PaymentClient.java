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

    @Step("Submit payment for order: {orderId}")
    public Response pay(String orderId, PaymentRequest paymentPayload) {
        Response response = http.post("/payments/" + orderId, paymentPayload);
        AllureSteps.attachJson("payment-response", response.asPrettyString());
        return response;
    }
}
