package com.smashzone.dto;

public record UserUpdateRequest(
    String fullName,
    String email,
    String phone,
    String address
) {}
