package com.example.foods.service;

import com.example.foods.constant.OrderStatus;
import com.example.foods.dto.request.CheckoutRequestDto;
import com.example.foods.dto.request.CreateOrderRequestDto;
import com.example.foods.dto.response.OrderResponseDto;
import java.util.List;

public interface OrderService {
  OrderResponseDto createOrder(Long userId, CreateOrderRequestDto orderDto);

  OrderResponseDto createOrderWithPayment(Long userId, CheckoutRequestDto checkoutDto);

  OrderResponseDto getOrderById(Long orderId);

  List<OrderResponseDto> getUserOrders(Long userId);

  OrderResponseDto updateOrderStatus(Long orderId, OrderStatus status);

  void cancelOrder(Long orderId);
}
