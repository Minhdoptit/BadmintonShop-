package com.smashzone.dto;

public record AddToCartRequest(
    Integer productId,
    Integer id,
    Integer quantity
) {
    public Integer effectiveProductId() {
        return productId != null ? productId : id;
    }
    public Integer effectiveQuantity() {
        return quantity != null && quantity > 0 ? quantity : 1;
    }
}
