package com.smashzone.dto;

import java.util.List;

public record OrderResponse(
    Integer id,
    Integer userId,
    Double totalAmount,
    String status,
    String shippingAddress,
    String paymentMethod,
    String createdAt,
    List<OrderDetailResponse> items
) {
    public Integer getOrderId() { return id; }
    public Double getTotalPrice() { return totalAmount; }
    public String getDeliveryAddress() { return shippingAddress; }
    public List<OrderDetailResponse> getOrderDetails() { return items; }
}
