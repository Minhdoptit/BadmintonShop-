package com.smashzone.dto;

public record CartItemDTO(
    Integer productId,
    String productName,
    String productImage,
    Double price,
    Integer quantity,
    Double subtotal
) {
    public Integer getId() { return productId; }
    public String getName() { return productName; }
    public String getImage() { return productImage; }
    public Double getUnitPrice() { return price; }
    public Double getTotalPrice() { return subtotal; }
    public String getWeight() { return "Chính hãng"; }
}
