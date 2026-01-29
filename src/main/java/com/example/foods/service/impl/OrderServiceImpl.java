package com.example.foods.service.impl;

import com.example.foods.constant.OrderStatus;
import com.example.foods.dto.request.CheckoutRequestDto;
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
import com.example.foods.service.PaymentService;
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
  private final PaymentService paymentService;

  @Override
  public OrderResponseDto createOrder(Long userId, CreateOrderRequestDto orderDto) {
    log.info("Creating order for user ID: {}", userId);

    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

    if (orderDto.getItems() == null || orderDto.getItems().isEmpty()) {
      throw new IllegalArgumentException("Order must contain at least one item");
    }

    Order order = Order.builder().user(user).totalAmount(0.0).status(OrderStatus.PENDING).build();

    double totalAmount = 0.0;

    for (var itemDto : orderDto.getItems()) {
      Food food =
          foodRepository
              .findById(itemDto.getFoodId())
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "Food not found with ID: " + itemDto.getFoodId()));
      Integer quantity = itemDto.getQuantity();
      if (quantity == null || quantity < 1) {
        throw new IllegalArgumentException(
            "Quantity must be at least 1 for food ID: " + itemDto.getFoodId());
      }

      if (food.getQuantity() < quantity) {
        throw new IllegalArgumentException(
            "Insufficient stock for food ID: " + itemDto.getFoodId() + 
            ". Available: " + food.getQuantity() + ", Requested: " + quantity);
      }

      OrderItem orderItem =
          OrderItem.builder()
              .order(order)
              .food(food)
              .quantity(quantity)
              .price(food.getPrice())
              .build();

      order.getItems().add(orderItem);
      totalAmount += food.getPrice() * quantity;
      
      // Decrement food inventory
      food.setQuantity(food.getQuantity() - quantity);
      foodRepository.save(food);
    }

    order.setTotalAmount(totalAmount);
    Order savedOrder = orderRepository.save(order);

    log.info("Order created successfully with ID: {}", savedOrder.getId());
    return orderMapper.toDto(savedOrder);
  }

  @Override
  public OrderResponseDto createOrderWithPayment(Long userId, CheckoutRequestDto checkoutDto) {
    log.info(
        "Creating order with payment for user ID: {}, payment method: {}",
        userId,
        checkoutDto.getPaymentMethod());

    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

    if (checkoutDto.getItems() == null || checkoutDto.getItems().isEmpty()) {
      throw new IllegalArgumentException("Order must contain at least one item");
    }

    // Create order with payment info
    Order order =
        Order.builder()
            .user(user)
            .totalAmount(0.0)
            .status(OrderStatus.PENDING)
            .paymentMethod(checkoutDto.getPaymentMethod())
            .paymentIntentId(checkoutDto.getPaymentIntentId())
            .paymentStatus("pending")
            .build();

    double totalAmount = 0.0;

    // Add order items
    for (var itemDto : checkoutDto.getItems()) {
      Food food =
          foodRepository
              .findById(itemDto.getFoodId())
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "Food not found with ID: " + itemDto.getFoodId()));

      Integer quantity = itemDto.getQuantity();
      if (quantity == null || quantity < 1) {
        throw new IllegalArgumentException(
            "Quantity must be at least 1 for food ID: " + itemDto.getFoodId());
      }

      if (!"ACTIVE".equals(food.getStatus())) {
        throw new IllegalArgumentException(
            "Food item is not available for order (status: " + food.getStatus() + 
            ") for food ID: " + itemDto.getFoodId());
      }

      if (food.getQuantity() < quantity) {
        throw new IllegalArgumentException(
            "Insufficient stock for food ID: " + itemDto.getFoodId() + 
            ". Available: " + food.getQuantity() + ", Requested: " + quantity);
      }

      OrderItem orderItem =
          OrderItem.builder()
              .order(order)
              .food(food)
              .quantity(quantity)
              .price(food.getPrice())
              .build();

      order.getItems().add(orderItem);
      totalAmount += food.getPrice() * quantity;
      
      food.setQuantity(food.getQuantity() - quantity);
      foodRepository.save(food);
    }

    order.setTotalAmount(totalAmount);

    // Handle payment confirmation for card payments
    if ("card".equals(checkoutDto.getPaymentMethod()) && checkoutDto.getPaymentIntentId() != null) {
      boolean paymentConfirmed = paymentService.confirmPayment(checkoutDto.getPaymentIntentId());

      if (paymentConfirmed) {
        order.setPaymentStatus("completed");
        order.setStatus(OrderStatus.PROCESSING);
        log.info("Card payment confirmed for order");
      } else {
        order.setPaymentStatus("failed");
        order.setStatus(OrderStatus.CANCELLED);
        log.warn("Card payment failed for order");
        throw new RuntimeException("Payment failed. Please try again.");
      }
    } else if ("cash".equals(checkoutDto.getPaymentMethod())) {
      order.setPaymentStatus("pending"); // Will be completed on delivery
      log.info("Cash on delivery order created");
    }

    Order savedOrder = orderRepository.save(order);
    log.info(
        "Order created successfully with ID: {} and payment method: {}",
        savedOrder.getId(),
        savedOrder.getPaymentMethod());

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
  public OrderResponseDto updateOrderStatus(Long orderId, OrderStatus status) {
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

    if (OrderStatus.DELIVERED.equals(order.getStatus())
        || OrderStatus.CANCELLED.equals(order.getStatus())) {
      throw new IllegalStateException("Cannot cancel order with status: " + order.getStatus());
    }

    order.setStatus(OrderStatus.CANCELLED);
    orderRepository.save(order);
    log.info("Order cancelled successfully");
  }
}
