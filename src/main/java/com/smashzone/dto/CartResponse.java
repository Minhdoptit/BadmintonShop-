package com.smashzone.dto;

import java.util.List;

public record CartResponse(
    List<CartItemDTO> items,
    int totalQuantity,
    double totalPrice
) {
    public double getTotalAmount() { return totalPrice; }
    public boolean getSuccess() { return true; }
    public String getMessage() { return "Thành công"; }
}
