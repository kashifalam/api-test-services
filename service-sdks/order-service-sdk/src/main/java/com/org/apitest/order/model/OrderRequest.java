package com.org.apitest.order.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderRequest {

    private String customerEmail;
    private String productId;
    private int quantity;
    private String correlationId;

    public String getCustomerEmail() {
        return customerEmail;
    }

    public OrderRequest setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
        return this;
    }

    public String getProductId() {
        return productId;
    }

    public OrderRequest setProductId(String productId) {
        this.productId = productId;
        return this;
    }

    public int getQuantity() {
        return quantity;
    }

    public OrderRequest setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public OrderRequest setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
        return this;
    }
}
