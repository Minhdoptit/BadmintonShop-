package com.smashzone.controller;

import com.smashzone.dto.*;
import com.smashzone.service.CategoryService;
import com.smashzone.service.ProductService;
import com.smashzone.service.RecommendationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ProductApiController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final RecommendationService recommendationService;

    public ProductApiController(
            ProductService productService,
            CategoryService categoryService,
            RecommendationService recommendationService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.recommendationService = recommendationService;
    }

    @GetMapping("/home/data")
    public ResponseEntity<HomeDataResponse> getHomeData(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");

        // Top trending products
        List<ProductResponse> all = productService.getAllProducts();
        List<ProductResponse> trending = all.stream().limit(8).toList();

        // Recommendations from AI engine
        List<RecommendationResponse> recs = recommendationService.getRecommendations(userId, 8);
        List<ProductResponse> recentRecs = new ArrayList<>();
        List<ProductResponse> collabRecs = new ArrayList<>();

        for (int i = 0; i < recs.size(); i++) {
            RecommendationResponse r = recs.get(i);
            try {
                ProductResponse p = productService.getProductById(r.productId());
                if (i % 2 == 0) {
                    recentRecs.add(p);
                } else {
                    collabRecs.add(p);
                }
            } catch (Exception ignored) {}
        }

        if (recentRecs.isEmpty()) recentRecs = trending.stream().limit(4).toList();
        if (collabRecs.isEmpty()) collabRecs = trending.stream().skip(4).limit(4).toList();

        return ResponseEntity.ok(new HomeDataResponse(recentRecs, collabRecs, trending));
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductResponse>> getProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer categoryId) {

        List<ProductResponse> products;
        if (search != null && !search.trim().isEmpty()) {
            products = productService.searchProducts(search.trim());
        } else if (category != null && !category.trim().isEmpty()) {
            products = productService.getProductsByCategoryName(category.trim());
        } else if (categoryId != null) {
            products = productService.getProductsByCategoryId(categoryId);
        } else {
            products = productService.getAllProducts();
        }
        return ResponseEntity.ok(products);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ProductDetailResponse> getProductById(@PathVariable Integer id, HttpSession session) {
        ProductResponse product = productService.getProductById(id);

        // Ghi nhận tương tác VIEW
        Integer userId = (Integer) session.getAttribute("userId");
        recommendationService.trackInteraction(userId, id, "VIEW");

        // Tìm sản phẩm tương tự cùng danh mục
        List<ProductResponse> similar = productService.getProductsByCategoryName(product.categoryName())
                .stream()
                .filter(p -> !p.id().equals(id))
                .limit(4)
                .toList();

        return ResponseEntity.ok(new ProductDetailResponse(product, similar));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponse>> getCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/recommendations")
    public ResponseEntity<List<RecommendationResponse>> getRecommendations(
            @RequestParam(defaultValue = "6") int limit,
            HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        return ResponseEntity.ok(recommendationService.getRecommendations(userId, limit));
    }

    @PostMapping("/interactions")
    public ResponseEntity<ApiResponse<Void>> trackInteraction(
            @RequestBody Map<String, Object> payload,
            HttpSession session) {
        try {
            Integer productId = (Integer) payload.get("productId");
            String type = (String) payload.getOrDefault("type", "VIEW");
            Integer userId = (Integer) session.getAttribute("userId");

            recommendationService.trackInteraction(userId, productId, type);
            return ResponseEntity.ok(ApiResponse.ok("Đã ghi nhận tương tác", null));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.ok("Tương tác bỏ qua", null));
        }
    }
}
