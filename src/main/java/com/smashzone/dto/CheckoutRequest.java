package com.smashzone.dto;

public record CheckoutRequest(
    String shippingAddress,
    String deliveryAddress,
    String paymentMethod,
    String notes
) {
    public String effectiveAddress() {
        if (shippingAddress != null && !shippingAddress.isBlank()) return shippingAddress;
        if (deliveryAddress != null && !deliveryAddress.isBlank()) return deliveryAddress;
        return "Địa chỉ nhận hàng mặc định";
    }
}
