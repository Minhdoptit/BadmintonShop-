package com.smashzone.dto;

public record UserResponse(
    Integer id,
    String username,
    String fullName,
    String email,
    String phone,
    String address,
    String role,
    String createdAt
) {
    public Integer getUserId() { return id; }
    public String getPhoneNumber() { return phone; }
    public String getDeliveryAddress() { return address; }
}
