package com.smashzone.controller;

import com.smashzone.dto.ApiResponse;
import com.smashzone.dto.ChangePasswordRequest;
import com.smashzone.dto.UserResponse;
import com.smashzone.dto.UserUpdateRequest;
import com.smashzone.entity.User;
import com.smashzone.exception.UnauthorizedException;
import com.smashzone.service.AuthService;
import com.smashzone.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileApiController {

    private final UserService userService;
    private final AuthService authService;

    public ProfileApiController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<UserResponse> getProfile(HttpSession session) {
        User user = authService.getCurrentUser(session);
        if (user == null) {
            throw new UnauthorizedException("Vui lòng đăng nhập");
        }
        return ResponseEntity.ok(userService.getUserById(user.getId()));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @RequestBody UserUpdateRequest request,
            HttpSession session) {
        User user = authService.getCurrentUser(session);
        if (user == null) {
            throw new UnauthorizedException("Vui lòng đăng nhập");
        }
        UserResponse response = userService.updateProfile(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật thông tin thành công", response));
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            HttpSession session) {
        User user = authService.getCurrentUser(session);
        if (user == null) {
            throw new UnauthorizedException("Vui lòng đăng nhập");
        }
        userService.changePassword(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok("Đổi mật khẩu thành công", null));
    }
}
