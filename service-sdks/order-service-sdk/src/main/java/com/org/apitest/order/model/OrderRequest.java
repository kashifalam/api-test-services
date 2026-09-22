package com.org.apitest.order.model;

/**
 * Order creation request payload. Matches the order-service {@code CreateOrderRequest} contract.
 */
public record OrderRequest(
        String userId,
        String item,
        int quantity,
        double amount) {
}
