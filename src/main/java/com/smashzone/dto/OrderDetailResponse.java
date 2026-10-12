package com.smashzone.dto;

public record OrderDetailResponse(
    Integer id,
    Integer productId,
    String productName,
    String productImage,
    Integer quantity,
    Double price,
    Double subtotal
) {
    public Integer getOrderDetailId() { return id; }
    public String getImage() { return productImage; }
    public Double getUnitPrice() { return price; }
    public Double getTotalPrice() { return subtotal; }
}
