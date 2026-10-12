package com.smashzone.dto;

import jakarta.validation.constraints.NotBlank;

public record OrderStatusUpdateRequest(
    @NotBlank(message = "Trạng thái không được để trống")
    String status
) {}
