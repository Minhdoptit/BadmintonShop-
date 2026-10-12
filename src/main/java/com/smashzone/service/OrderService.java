package com.smashzone.service;

import com.smashzone.dto.CheckoutRequest;
import com.smashzone.dto.OrderResponse;
import com.smashzone.dto.OrderStatusUpdateRequest;
import jakarta.servlet.http.HttpSession;
import java.util.List;

public interface OrderService {
    OrderResponse checkout(CheckoutRequest request, HttpSession session);
    List<OrderResponse> getOrdersByUserId(Integer userId);
    List<OrderResponse> getAllOrders();
    OrderResponse getOrderById(Integer id);
    OrderResponse updateOrderStatus(Integer id, OrderStatusUpdateRequest request);
}
