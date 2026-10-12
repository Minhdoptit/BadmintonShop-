package com.smashzone.controller;

import com.smashzone.dto.ApiResponse;
import com.smashzone.dto.LoginRequest;
import com.smashzone.dto.RegisterRequest;
import com.smashzone.dto.UserResponse;
import com.smashzone.entity.User;
import com.smashzone.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest request, HttpSession session) {
        UserResponse response = authService.login(request, session);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("message", "Đăng nhập thành công");
        res.put("user", response);
        res.put("data", response);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse response = authService.register(request);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("message", "Đăng ký tài khoản thành công");
        res.put("user", response);
        res.put("data", response);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpSession session) {
        authService.logout(session);
        return ResponseEntity.ok(ApiResponse.ok("Đăng xuất thành công", null));
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(HttpSession session) {
        Map<String, Object> res = new HashMap<>();
        User user = authService.getCurrentUser(session);
        if (user != null) {
            res.put("authenticated", true);
            res.put("user", authService.getCurrentUserResponse(session));
        } else {
            res.put("authenticated", false);
            res.put("user", null);
        }
        return ResponseEntity.ok(res);
    }
}
