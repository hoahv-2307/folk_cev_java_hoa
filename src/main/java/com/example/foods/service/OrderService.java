package com.example.foods.service;

import com.example.foods.dto.request.CreateOrderRequestDto;
import com.example.foods.dto.response.OrderResponseDto;
import java.util.List;

public interface OrderService {
  OrderResponseDto createOrder(Long userId, CreateOrderRequestDto orderDto);

  OrderResponseDto getOrderById(Long orderId);

  List<OrderResponseDto> getUserOrders(Long userId);

  OrderResponseDto updateOrderStatus(Long orderId, String status);

  void cancelOrder(Long orderId);
}
