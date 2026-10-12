package com.smashzone.service.impl;

import com.smashzone.dto.LoginRequest;
import com.smashzone.dto.RegisterRequest;
import com.smashzone.dto.UserResponse;
import com.smashzone.entity.User;
import com.smashzone.exception.BadRequestException;
import com.smashzone.exception.ResourceNotFoundException;
import com.smashzone.exception.UnauthorizedException;
import com.smashzone.repository.UserRepository;
import com.smashzone.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public AuthServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private String hashMD5(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return password;
        }
    }

    private String hashSHA256(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return password;
        }
    }

    private boolean verifyPassword(String inputPassword, String storedHash, String username) {
        if (inputPassword == null || storedHash == null) return false;

        // 1. So sánh trực tiếp plain text (Ví dụ mật khẩu admin là 123456 hoặc 1)
        if (inputPassword.equals(storedHash)) {
            return true;
        }

        // Hỗ trợ tiện ích cho tài khoản admin (chấp nhận cả '1', '123456', 'admin')
        if ("admin".equalsIgnoreCase(username) && ("1".equals(inputPassword) || "123456".equals(inputPassword) || "admin".equals(inputPassword))) {
            return true;
        }

        // 2. So sánh mã băm MD5 (các tài khoản có hash MD5 của '123': 202cb962ac59075b964b07152d234b70)
        String md5 = hashMD5(inputPassword);
        if (md5 != null && md5.equalsIgnoreCase(storedHash)) {
            return true;
        }

        // 3. So sánh mã băm SHA-256
        String sha256 = hashSHA256(inputPassword);
        if (sha256 != null && sha256.equalsIgnoreCase(storedHash)) {
            return true;
        }

        return false;
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
    public UserResponse login(LoginRequest request, HttpSession session) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BadRequestException("Tên đăng nhập không tồn tại"));

        if (!verifyPassword(request.password(), user.getPassword(), user.getUsername())) {
            throw new BadRequestException("Mật khẩu không chính xác");
        }

        session.setAttribute("user", user);
        session.setAttribute("userId", user.getId());
        session.setAttribute("role", user.getRole());

        return mapToResponse(user);
    }

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BadRequestException("Tên đăng nhập đã tồn tại trong hệ thống");
        }
        if (request.email() != null && !request.email().isBlank() && userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email đã được sử dụng bởi tài khoản khác");
        }

        User user = new User();
        user.setUsername(request.username());
        // Lưu theo chuẩn MD5 tương thích hệ thống
        user.setPassword(hashMD5(request.password()));
        user.setFullName(request.fullName() != null ? request.fullName() : request.username());
        user.setEmail(request.email() != null ? request.email() : request.username() + "@smashzone.vn");
        user.setPhone(request.phone());
        user.setAddress(request.address() != null ? request.address() : "Hà Nội");
        user.setRole("CUSTOMER");
        user.setCreatedAt(LocalDateTime.now());

        User saved = userRepository.save(user);
        return mapToResponse(saved);
    }

    @Override
    public void logout(HttpSession session) {
        session.invalidate();
    }

    @Override
    public User getCurrentUser(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return null;
        }
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public UserResponse getCurrentUserResponse(HttpSession session) {
        User user = getCurrentUser(session);
        if (user == null) {
            throw new UnauthorizedException("Vui lòng đăng nhập để tiếp tục");
        }
        return mapToResponse(user);
    }
}
