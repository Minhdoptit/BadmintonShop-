package com.smashzone.controller;

import com.smashzone.dto.*;
import com.smashzone.entity.User;
import com.smashzone.exception.UnauthorizedException;
import com.smashzone.service.AuthService;
import com.smashzone.service.OrderService;
import com.smashzone.service.ProductService;
import com.smashzone.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminApiController {

    private final AuthService authService;
    private final UserService userService;
    private final ProductService productService;
    private final OrderService orderService;

    public AdminApiController(
            AuthService authService,
            UserService userService,
            ProductService productService,
            OrderService orderService) {
        this.authService = authService;
        this.userService = userService;
        this.productService = productService;
        this.orderService = orderService;
    }

    private void checkAdmin(HttpSession session) {
        User user = authService.getCurrentUser(session);
        if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
            throw new UnauthorizedException("Chức năng chỉ dành riêng cho Quản trị viên (Admin)");
        }
    }

    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardStats> getDashboardStats(HttpSession session) {
        checkAdmin(session);
        return ResponseEntity.ok(userService.getDashboardStats());
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductResponse>> getAllProducts(HttpSession session) {
        checkAdmin(session);
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @PostMapping("/products")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody ProductCreateRequest request,
            HttpSession session) {
        checkAdmin(session);
        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.ok(ApiResponse.ok("Thêm sản phẩm thành công", response));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Integer id,
            @Valid @RequestBody ProductCreateRequest request,
            HttpSession session) {
        checkAdmin(session);
        ProductResponse response = productService.updateProduct(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật sản phẩm thành công", response));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Integer id, HttpSession session) {
        checkAdmin(session);
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.ok("Xóa sản phẩm thành công", null));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getAllOrders(HttpSession session) {
        checkAdmin(session);
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Integer id,
            @Valid @RequestBody OrderStatusUpdateRequest request,
            HttpSession session) {
        checkAdmin(session);
        OrderResponse response = orderService.updateOrderStatus(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật trạng thái đơn hàng thành công", response));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers(HttpSession session) {
        checkAdmin(session);
        return ResponseEntity.ok(userService.getAllCustomers());
    }
}
