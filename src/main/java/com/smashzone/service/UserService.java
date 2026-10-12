package com.smashzone.service;

import com.smashzone.dto.AdminDashboardStats;
import com.smashzone.dto.ChangePasswordRequest;
import com.smashzone.dto.UserResponse;
import com.smashzone.dto.UserUpdateRequest;
import java.util.List;

public interface UserService {
    UserResponse getUserById(Integer id);
    UserResponse updateProfile(Integer id, UserUpdateRequest request);
    void changePassword(Integer id, ChangePasswordRequest request);
    List<UserResponse> getAllCustomers();
    AdminDashboardStats getDashboardStats();
}
