package com.smashzone.controller;

import com.smashzone.dto.ApiResponse;
import com.smashzone.dto.CheckoutRequest;
import com.smashzone.dto.OrderResponse;
import com.smashzone.entity.User;
import com.smashzone.exception.UnauthorizedException;
import com.smashzone.service.AuthService;
import com.smashzone.service.OrderService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderApiController {

    private final OrderService orderService;
    private final AuthService authService;

    public OrderApiController(OrderService orderService, AuthService authService) {
        this.orderService = orderService;
        this.authService = authService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<OrderResponse>> checkout(
            @Valid @RequestBody CheckoutRequest request,
            HttpSession session) {
        OrderResponse response = orderService.checkout(request, session);
        return ResponseEntity.ok(ApiResponse.ok("Đặt hàng thành công", response));
    }

    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderResponse>> getMyOrders(HttpSession session) {
        User user = authService.getCurrentUser(session);
        if (user == null) {
            throw new UnauthorizedException("Vui lòng đăng nhập để xem lịch sử đơn hàng");
        }
        return ResponseEntity.ok(orderService.getOrdersByUserId(user.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Integer id, HttpSession session) {
        User user = authService.getCurrentUser(session);
        if (user == null) {
            throw new UnauthorizedException("Vui lòng đăng nhập");
        }
        OrderResponse order = orderService.getOrderById(id);
        if (!"ADMIN".equalsIgnoreCase(user.getRole()) && !order.userId().equals(user.getId())) {
            throw new UnauthorizedException("Bạn không có quyền xem đơn hàng này");
        }
        return ResponseEntity.ok(order);
    }
}
