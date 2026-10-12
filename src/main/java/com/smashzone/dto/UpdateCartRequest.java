package com.smashzone.dto;

public record UpdateCartRequest(
    Integer productId,
    Integer id,
    Integer quantity
) {
    public Integer effectiveProductId() {
        return productId != null ? productId : id;
    }
}
