package com.example.foods.service.impl;

import com.example.foods.dto.request.CreateOrderRequestDto;
import com.example.foods.dto.response.OrderResponseDto;
import com.example.foods.entity.Food;
import com.example.foods.entity.Order;
import com.example.foods.entity.OrderItem;
import com.example.foods.entity.User;
import com.example.foods.mapper.OrderMapper;
import com.example.foods.repository.FoodRepository;
import com.example.foods.repository.OrderRepository;
import com.example.foods.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderServiceImpl implements com.example.foods.service.OrderService {

  private final OrderRepository orderRepository;
  private final UserRepository userRepository;
  private final FoodRepository foodRepository;
  private final OrderMapper orderMapper;

  @Override
  public OrderResponseDto createOrder(Long userId, CreateOrderRequestDto orderDto) {
    log.info("Creating order for user ID: {}", userId);

    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

    Order order = Order.builder().user(user).totalAmount(0.0).status("PENDING").build();

    double totalAmount = 0.0;

    for (var itemDto : orderDto.getItems()) {
      Food food =
          foodRepository
              .findById(itemDto.getFoodId())
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "Food not found with ID: " + itemDto.getFoodId()));

      OrderItem orderItem =
          OrderItem.builder()
              .order(order)
              .food(food)
              .quantity(itemDto.getQuantity())
              .price(food.getPrice())
              .build();

      order.getItems().add(orderItem);
      totalAmount += food.getPrice() * itemDto.getQuantity();
    }

    order.setTotalAmount(totalAmount);
    Order savedOrder = orderRepository.save(order);

    log.info("Order created successfully with ID: {}", savedOrder.getId());
    return orderMapper.toDto(savedOrder);
  }

  @Override
  @Transactional(readOnly = true)
  public OrderResponseDto getOrderById(Long orderId) {
    log.info("Fetching order with ID: {}", orderId);
    Order order =
        orderRepository
            .findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));
    return orderMapper.toDto(order);
  }

  @Override
  @Transactional(readOnly = true)
  public List<OrderResponseDto> getUserOrders(Long userId) {
    log.info("Fetching orders for user ID: {}", userId);
    List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    return orderMapper.toDtoList(orders);
  }

  @Override
  public OrderResponseDto updateOrderStatus(Long orderId, String status) {
    log.info("Updating order {} status to: {}", orderId, status);
    Order order =
        orderRepository
            .findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));

    order.setStatus(status);
    Order updatedOrder = orderRepository.save(order);
    return orderMapper.toDto(updatedOrder);
  }

  @Override
  public void cancelOrder(Long orderId) {
    log.info("Cancelling order with ID: {}", orderId);
    Order order =
        orderRepository
            .findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));

    if ("DELIVERED".equals(order.getStatus()) || "CANCELLED".equals(order.getStatus())) {
      throw new IllegalStateException("Cannot cancel order with status: " + order.getStatus());
    }

    order.setStatus("CANCELLED");
    orderRepository.save(order);
    log.info("Order cancelled successfully");
  }
}
