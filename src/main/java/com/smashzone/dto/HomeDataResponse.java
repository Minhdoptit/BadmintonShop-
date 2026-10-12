package com.smashzone.dto;

import java.util.List;

public record HomeDataResponse(
    List<ProductResponse> recentRecommendations,
    List<ProductResponse> collaborativeRecommendations,
    List<ProductResponse> trendingProducts
) {}
