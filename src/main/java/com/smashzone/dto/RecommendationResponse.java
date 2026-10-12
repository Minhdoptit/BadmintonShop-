package com.smashzone.dto;

public record RecommendationResponse(
    Integer id,
    Integer productId,
    String productName,
    String productImage,
    Double price,
    Double score,
    String reason
) {}
