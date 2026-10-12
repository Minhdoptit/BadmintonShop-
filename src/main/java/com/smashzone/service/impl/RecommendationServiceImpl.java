package com.smashzone.service.impl;

import com.smashzone.dto.RecommendationResponse;
import com.smashzone.entity.Product;
import com.smashzone.entity.ProductRecommendation;
import com.smashzone.entity.UserInteraction;
import com.smashzone.repository.ProductRecommendationRepository;
import com.smashzone.repository.ProductRepository;
import com.smashzone.repository.UserInteractionRepository;
import com.smashzone.service.RecommendationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    private final UserInteractionRepository interactionRepository;
    private final ProductRecommendationRepository recommendationRepository;
    private final ProductRepository productRepository;

    public RecommendationServiceImpl(
            UserInteractionRepository interactionRepository,
            ProductRecommendationRepository recommendationRepository,
            ProductRepository productRepository) {
        this.interactionRepository = interactionRepository;
        this.recommendationRepository = recommendationRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public void trackInteraction(Integer userId, Integer productId, String interactionType) {
        if (userId == null || productId == null) return;

        double score = switch (interactionType.toUpperCase()) {
            case "PURCHASE" -> 5.0;
            case "CART" -> 3.0;
            case "CLICK" -> 2.0;
            default -> 1.0; // VIEW
        };

        UserInteraction interaction = new UserInteraction(userId, productId, interactionType.toUpperCase(), score);
        interactionRepository.save(interaction);
    }

    @Override
    @Transactional
    public List<RecommendationResponse> getRecommendations(Integer userId, int limit) {
        List<Product> allProducts = productRepository.findAll();
        if (allProducts.isEmpty()) {
            return Collections.emptyList();
        }

        // Nếu khách vãng lai (chưa đăng nhập), gợi ý các sản phẩm nổi bật nhất
        if (userId == null) {
            return allProducts.stream()
                    .limit(limit)
                    .map(p -> new RecommendationResponse(0, p.getId(), p.getName(), p.getImage(), p.getPrice(), 1.0, "Sản phẩm được mua nhiều nhất"))
                    .toList();
        }

        // Lấy lịch sử tương tác của người dùng
        List<UserInteraction> userInteractions = interactionRepository.findByUserIdOrderByCreatedAtDesc(userId);

        if (userInteractions.isEmpty()) {
            // Cold-start problem: chưa có tương tác -> gợi ý top sản phẩm
            return allProducts.stream()
                    .limit(limit)
                    .map(p -> new RecommendationResponse(0, p.getId(), p.getName(), p.getImage(), p.getPrice(), 1.0, "Gợi ý dành riêng cho người mới"))
                    .toList();
        }

        // 1. Phân tích sở thích dựa trên Nội dung (Content-Based Filtering)
        // Tìm các danh mục và từ khóa mà user tương tác nhiều nhất
        Map<Integer, Double> userProductWeights = new HashMap<>();
        for (UserInteraction ui : userInteractions) {
            userProductWeights.merge(ui.getProductId(), ui.getInteractionScore(), Double::sum);
        }

        Set<Integer> interactedProductIds = userProductWeights.keySet();

        // Thu thập đặc trưng các sản phẩm đã tương tác
        Set<String> preferredCategories = new HashSet<>();
        List<String> preferredKeywords = new ArrayList<>();

        for (Integer pid : interactedProductIds) {
            productRepository.findById(pid).ifPresent(p -> {
                if (p.getCategory() != null) preferredCategories.add(p.getCategory().getName().toLowerCase());
                if (p.getMetadataForAi() != null) {
                    preferredKeywords.addAll(Arrays.asList(p.getMetadataForAi().toLowerCase().split("[\\s,]+")));
                }
            });
        }

        // 2. Tính điểm tương đồng Cosine / Jaccard cho các sản phẩm ứng viên (chưa mua)
        Map<Product, Double> candidateScores = new HashMap<>();

        for (Product candidate : allProducts) {
            if (interactedProductIds.contains(candidate.getId())) continue;

            double score = 0.0;
            String reason = "Phù hợp với sở thích của bạn";

            // Điểm trùng danh mục
            if (candidate.getCategory() != null && preferredCategories.contains(candidate.getCategory().getName().toLowerCase())) {
                score += 3.0;
                reason = "Cùng dòng sản phẩm bạn quan tâm: " + candidate.getCategory().getName();
            }

            // Điểm tương đồng từ khóa metadata AI
            if (candidate.getMetadataForAi() != null) {
                String meta = candidate.getMetadataForAi().toLowerCase();
                for (String kw : preferredKeywords) {
                    if (kw.length() > 2 && meta.contains(kw)) {
                        score += 0.5;
                    }
                }
            }

            candidateScores.put(candidate, score);
        }

        // Sắp xếp giảm dần theo điểm số
        List<Map.Entry<Product, Double>> topCandidates = candidateScores.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .limit(limit)
                .toList();

        // Cập nhật bảng recommendations trong database
        recommendationRepository.deleteByUserId(userId);

        List<RecommendationResponse> responses = new ArrayList<>();
        for (Map.Entry<Product, Double> entry : topCandidates) {
            Product p = entry.getKey();
            Double score = entry.getValue();
            String reason = score > 2.0 ? "Gợi ý thông minh dựa trên hành vi xem và mua sắm" : "Sản phẩm xu hướng đề xuất cho bạn";

            ProductRecommendation rec = new ProductRecommendation(userId, p.getId(), score, reason);
            recommendationRepository.save(rec);

            responses.add(new RecommendationResponse(rec.getId(), p.getId(), p.getName(), p.getImage(), p.getPrice(), score, reason));
        }

        return responses;
    }
}
