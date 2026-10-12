package com.smashzone.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProductCreateRequest(
    Integer categoryId,

    @NotBlank(message = "Tên sản phẩm không được để trống")
    String name,

    @NotNull(message = "Giá không được để trống")
    @Positive(message = "Giá phải lớn hơn 0")
    Double price,

    String productLink,
    String image,
    Integer quantity,
    Double importPrice,
    String metadataForAi
) {}
