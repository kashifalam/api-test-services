package com.org.apitest.payment;

import com.org.apitest.http.HttpClientFacade;
import com.org.apitest.observability.AllureSteps;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.Map;

public class PaymentClient {

    private final HttpClientFacade http;

    public PaymentClient() {
        this.http = HttpClientFacade.forService("payment");
    }

    @Step("Submit payment for order: {orderId}")
    public Response pay(String orderId, Map<String, Object> paymentPayload) {
        Response response = http.post("/payments/" + orderId, paymentPayload);
        AllureSteps.attachJson("payment-response", response.asPrettyString());
        return response;
    }
}
