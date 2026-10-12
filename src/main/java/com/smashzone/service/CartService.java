package com.smashzone.service;

import com.smashzone.dto.AddToCartRequest;
import com.smashzone.dto.CartResponse;
import com.smashzone.dto.UpdateCartRequest;
import jakarta.servlet.http.HttpSession;

public interface CartService {
    CartResponse getCart(HttpSession session);
    CartResponse addToCart(AddToCartRequest request, HttpSession session);
    CartResponse updateCart(UpdateCartRequest request, HttpSession session);
    CartResponse removeItem(Integer productId, HttpSession session);
    void clearCart(HttpSession session);
}
