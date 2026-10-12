package com.smashzone.service.impl;

import com.smashzone.dto.*;
import com.smashzone.entity.Order;
import com.smashzone.entity.OrderDetail;
import com.smashzone.entity.Product;
import com.smashzone.entity.User;
import com.smashzone.exception.BadRequestException;
import com.smashzone.exception.ResourceNotFoundException;
import com.smashzone.exception.UnauthorizedException;
import com.smashzone.repository.OrderDetailRepository;
import com.smashzone.repository.OrderRepository;
import com.smashzone.repository.ProductRepository;
import com.smashzone.service.AuthService;
import com.smashzone.service.CartService;
import com.smashzone.service.OrderService;
import com.smashzone.service.RecommendationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final AuthService authService;
    private final CartService cartService;
    private final RecommendationService recommendationService;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public OrderServiceImpl(
            OrderRepository orderRepository,
            OrderDetailRepository orderDetailRepository,
            ProductRepository productRepository,
            AuthService authService,
            CartService cartService,
            RecommendationService recommendationService) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.productRepository = productRepository;
        this.authService = authService;
        this.cartService = cartService;
        this.recommendationService = recommendationService;
    }

    private OrderResponse mapToResponse(Order order) {
        List<OrderDetail> details = order.getOrderDetails() != null && !order.getOrderDetails().isEmpty()
                ? order.getOrderDetails()
                : orderDetailRepository.findByOrderId(order.getId());

        List<OrderDetailResponse> itemResponses = new ArrayList<>();
        for (OrderDetail d : details) {
            Product p = productRepository.findById(d.getProductId()).orElse(null);
            String pName = p != null ? p.getName() : "Sản phẩm #" + d.getProductId();
            String pImg = p != null ? p.getImage() : "";
            double sub = d.getPrice() * d.getQuantity();
            itemResponses.add(new OrderDetailResponse(d.getId(), d.getProductId(), pName, pImg, d.getQuantity(), d.getPrice(), sub));
        }

        String createdStr = order.getCreatedAt() != null ? order.getCreatedAt().format(FORMATTER) : "";
        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getShippingAddress(),
                order.getPaymentMethod(),
                createdStr,
                itemResponses
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderResponse checkout(CheckoutRequest request, HttpSession session) {
        User user = authService.getCurrentUser(session);
        if (user == null) {
            throw new UnauthorizedException("Vui lòng đăng nhập trước khi thanh toán đơn hàng");
        }

        CartResponse cart = cartService.getCart(session);
        if (cart.items().isEmpty()) {
            throw new BadRequestException("Giỏ hàng của bạn đang trống");
        }

        // 1. Kiểm tra tồn kho và trừ kho an toàn
        for (CartItemDTO item : cart.items()) {
            Product p = productRepository.findById(item.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không tồn tại: ID " + item.productId()));
            if (p.getQuantity() != null && p.getQuantity() < item.quantity()) {
                throw new BadRequestException("Sản phẩm '" + p.getName() + "' không đủ số lượng trong kho (còn " + p.getQuantity() + ")");
            }
            p.setQuantity(p.getQuantity() - item.quantity());
            productRepository.save(p);
        }

        // 2. Tạo đơn hàng Order
        Order order = new Order();
        order.setUserId(user.getId());
        order.setTotalAmount(cart.totalPrice());
        order.setStatus("PENDING");
        order.setShippingAddress(request.effectiveAddress());
        order.setPaymentMethod(request.paymentMethod() != null ? request.paymentMethod() : "COD");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        // 3. Tạo OrderDetail và ghi nhận tương tác PURCHASE cho AI engine
        List<OrderDetail> details = new ArrayList<>();
        for (CartItemDTO item : cart.items()) {
            OrderDetail detail = new OrderDetail(savedOrder, item.productId(), item.quantity(), item.price());
            details.add(detail);
            orderDetailRepository.save(detail);

            recommendationService.trackInteraction(user.getId(), item.productId(), "PURCHASE");
        }

        savedOrder.setOrderDetails(details);
        cartService.clearCart(session);

        return mapToResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUserId(Integer userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc().stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Integer id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với ID: " + id));
        return mapToResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Integer id, OrderStatusUpdateRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với ID: " + id));

        order.setStatus(request.status());
        order.setUpdatedAt(LocalDateTime.now());
        Order updated = orderRepository.save(order);
        return mapToResponse(updated);
    }
}
