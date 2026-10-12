package com.smashzone.service;

import com.smashzone.dto.LoginRequest;
import com.smashzone.dto.RegisterRequest;
import com.smashzone.dto.UserResponse;
import com.smashzone.entity.User;
import jakarta.servlet.http.HttpSession;

public interface AuthService {
    UserResponse login(LoginRequest request, HttpSession session);
    UserResponse register(RegisterRequest request);
    void logout(HttpSession session);
    User getCurrentUser(HttpSession session);
    UserResponse getCurrentUserResponse(HttpSession session);
}
