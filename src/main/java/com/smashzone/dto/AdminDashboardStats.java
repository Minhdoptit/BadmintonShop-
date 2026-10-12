package com.smashzone.dto;

import java.util.List;

public record AdminDashboardStats(
    long totalUsers,
    long totalProducts,
    long totalOrders,
    double totalRevenue,
    List<OrderResponse> recentOrders
) {}
