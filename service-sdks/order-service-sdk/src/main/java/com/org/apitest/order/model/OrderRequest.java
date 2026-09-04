package com.org.apitest.order.model;

/**
 * Order creation request payload.
 */
public record OrderRequest(
        String customerEmail,
        String productId,
        int quantity,
        String correlationId) {
}
