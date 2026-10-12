package com.smashzone.service.impl;

import com.smashzone.dto.AdminDashboardStats;
import com.smashzone.dto.ChangePasswordRequest;
import com.smashzone.dto.OrderResponse;
import com.smashzone.dto.UserResponse;
import com.smashzone.dto.UserUpdateRequest;
import com.smashzone.entity.User;
import com.smashzone.exception.BadRequestException;
import com.smashzone.exception.ResourceNotFoundException;
import com.smashzone.repository.OrderRepository;
import com.smashzone.repository.ProductRepository;
import com.smashzone.repository.UserRepository;
import com.smashzone.service.OrderService;
import com.smashzone.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public UserServiceImpl(
            UserRepository userRepository,
            ProductRepository productRepository,
            OrderRepository orderRepository,
            OrderService orderService) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return password;
        }
    }

    private UserResponse mapToResponse(User user) {
        String createdStr = user.getCreatedAt() != null ? user.getCreatedAt().format(FORMATTER) : "";
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getAddress(),
                user.getRole(),
                createdStr
        );
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + id));
        return mapToResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateProfile(Integer id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + id));

        if (request.fullName() != null) user.setFullName(request.fullName());
        if (request.email() != null) user.setEmail(request.email());
        if (request.phone() != null) user.setPhone(request.phone());
        if (request.address() != null) user.setAddress(request.address());

        User saved = userRepository.save(user);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public void changePassword(Integer id, ChangePasswordRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + id));

        String hashedCurrent = hashPassword(request.currentPassword());
        if (!user.getPassword().equals(hashedCurrent) && !user.getPassword().equals(request.currentPassword())) {
            throw new BadRequestException("Mật khẩu hiện tại không đúng");
        }

        user.setPassword(hashPassword(request.newPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllCustomers() {
        return userRepository.findByRole("CUSTOMER").stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AdminDashboardStats getDashboardStats() {
        long totalUsers = userRepository.count();
        long totalProducts = productRepository.count();
        long totalOrders = orderRepository.count();

        List<OrderResponse> allOrders = orderService.getAllOrders();
        double totalRevenue = allOrders.stream()
                .filter(o -> !"CANCELLED".equalsIgnoreCase(o.status()))
                .mapToDouble(OrderResponse::totalAmount)
                .sum();

        List<OrderResponse> recentOrders = allOrders.stream().limit(5).toList();

        return new AdminDashboardStats(totalUsers, totalProducts, totalOrders, totalRevenue, recentOrders);
    }
}
