package com.smashzone.controller;

import com.smashzone.dto.AddToCartRequest;
import com.smashzone.dto.CartResponse;
import com.smashzone.dto.UpdateCartRequest;
import com.smashzone.exception.UnauthorizedException;
import com.smashzone.service.CartService;
import com.smashzone.service.RecommendationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartApiController {

    private final CartService cartService;
    private final RecommendationService recommendationService;

    public CartApiController(CartService cartService, RecommendationService recommendationService) {
        this.cartService = cartService;
        this.recommendationService = recommendationService;
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart(HttpSession session) {
        if (session.getAttribute("user") == null) {
            return ResponseEntity.ok(new CartResponse(List.of(), 0, 0.0));
        }
        return ResponseEntity.ok(cartService.getCart(session));
    }

    @PostMapping("/add")
    public ResponseEntity<CartResponse> addToCart(@RequestBody AddToCartRequest request, HttpSession session) {
        if (session.getAttribute("user") == null) {
            throw new UnauthorizedException("Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng");
        }
        CartResponse response = cartService.addToCart(request, session);
        Integer userId = (Integer) session.getAttribute("userId");
        recommendationService.trackInteraction(userId, request.effectiveProductId(), "CART");
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/update", method = {RequestMethod.POST, RequestMethod.PUT})
    public ResponseEntity<CartResponse> updateCart(@RequestBody UpdateCartRequest request, HttpSession session) {
        if (session.getAttribute("user") == null) {
            throw new UnauthorizedException("Vui lòng đăng nhập để thao tác giỏ hàng");
        }
        return ResponseEntity.ok(cartService.updateCart(request, session));
    }

    @PostMapping("/remove")
    public ResponseEntity<CartResponse> removePost(@RequestBody Map<String, Object> body, HttpSession session) {
        if (session.getAttribute("user") == null) {
            throw new UnauthorizedException("Vui lòng đăng nhập để thao tác giỏ hàng");
        }
        Object val = body.get("productId");
        Integer pId = val instanceof Integer ? (Integer) val : Integer.parseInt(val.toString());
        return ResponseEntity.ok(cartService.removeItem(pId, session));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<CartResponse> removeItem(@PathVariable Integer productId, HttpSession session) {
        if (session.getAttribute("user") == null) {
            throw new UnauthorizedException("Vui lòng đăng nhập để thao tác giỏ hàng");
        }
        return ResponseEntity.ok(cartService.removeItem(productId, session));
    }

    @RequestMapping(value = "/clear", method = {RequestMethod.POST, RequestMethod.DELETE})
    public ResponseEntity<CartResponse> clearCart(HttpSession session) {
        if (session.getAttribute("user") == null) {
            throw new UnauthorizedException("Vui lòng đăng nhập để thao tác giỏ hàng");
        }
        cartService.clearCart(session);
        return ResponseEntity.ok(cartService.getCart(session));
    }
}
