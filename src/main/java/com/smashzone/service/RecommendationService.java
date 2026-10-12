package com.smashzone.service;

import com.smashzone.dto.RecommendationResponse;
import java.util.List;

public interface RecommendationService {
    void trackInteraction(Integer userId, Integer productId, String interactionType);
    List<RecommendationResponse> getRecommendations(Integer userId, int limit);
}
