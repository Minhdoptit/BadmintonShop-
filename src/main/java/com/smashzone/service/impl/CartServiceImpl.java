package com.smashzone.service.impl;

import com.smashzone.dto.AddToCartRequest;
import com.smashzone.dto.CartItemDTO;
import com.smashzone.dto.CartResponse;
import com.smashzone.dto.UpdateCartRequest;
import com.smashzone.entity.Product;
import com.smashzone.exception.BadRequestException;
import com.smashzone.exception.ResourceNotFoundException;
import com.smashzone.repository.ProductRepository;
import com.smashzone.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartServiceImpl implements CartService {

    private static final String CART_SESSION_KEY = "cart_items";
    private final ProductRepository productRepository;

    public CartServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @SuppressWarnings("unchecked")
    private Map<Integer, CartItemDTO> getCartMap(HttpSession session) {
        Map<Integer, CartItemDTO> cart = (Map<Integer, CartItemDTO>) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new LinkedHashMap<>();
            session.setAttribute(CART_SESSION_KEY, cart);
        }
        return cart;
    }

    private CartResponse buildCartResponse(Map<Integer, CartItemDTO> cart) {
        List<CartItemDTO> items = new ArrayList<>(cart.values());
        int totalQty = items.stream().mapToInt(CartItemDTO::quantity).sum();
        double totalPrice = items.stream().mapToDouble(CartItemDTO::subtotal).sum();
        return new CartResponse(items, totalQty, totalPrice);
    }

    @Override
    public CartResponse getCart(HttpSession session) {
        return buildCartResponse(getCartMap(session));
    }

    @Override
    public CartResponse addToCart(AddToCartRequest request, HttpSession session) {
        Integer pId = request.effectiveProductId();
        int qty = request.effectiveQuantity();

        Product product = productRepository.findById(pId)
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không tồn tại: ID " + pId));

        if (product.getQuantity() != null && product.getQuantity() < qty) {
            throw new BadRequestException("Số lượng tồn kho không đủ (còn lại: " + product.getQuantity() + ")");
        }

        Map<Integer, CartItemDTO> cart = getCartMap(session);
        int currentQty = cart.containsKey(pId) ? cart.get(pId).quantity() : 0;
        int newQty = currentQty + qty;

        double price = product.getPrice() != null ? product.getPrice() : 0.0;
        double subtotal = price * newQty;

        CartItemDTO item = new CartItemDTO(
                product.getId(),
                product.getName(),
                product.getImage(),
                price,
                newQty,
                subtotal
        );

        cart.put(product.getId(), item);
        session.setAttribute(CART_SESSION_KEY, cart);
        return buildCartResponse(cart);
    }

    @Override
    public CartResponse updateCart(UpdateCartRequest request, HttpSession session) {
        Integer pId = request.effectiveProductId();
        Map<Integer, CartItemDTO> cart = getCartMap(session);
        if (!cart.containsKey(pId)) {
            throw new ResourceNotFoundException("Sản phẩm không có trong giỏ hàng");
        }

        if (request.quantity() == null || request.quantity() <= 0) {
            cart.remove(pId);
        } else {
            CartItemDTO old = cart.get(pId);
            double subtotal = old.price() * request.quantity();
            cart.put(pId, new CartItemDTO(old.productId(), old.productName(), old.productImage(), old.price(), request.quantity(), subtotal));
        }

        session.setAttribute(CART_SESSION_KEY, cart);
        return buildCartResponse(cart);
    }

    @Override
    public CartResponse removeItem(Integer productId, HttpSession session) {
        Map<Integer, CartItemDTO> cart = getCartMap(session);
        cart.remove(productId);
        session.setAttribute(CART_SESSION_KEY, cart);
        return buildCartResponse(cart);
    }

    @Override
    public void clearCart(HttpSession session) {
        session.removeAttribute(CART_SESSION_KEY);
    }
}
