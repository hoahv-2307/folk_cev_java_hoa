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
import com.example.foods.service.MailService;
import com.example.foods.service.PaymentService;
import jakarta.persistence.OptimisticLockException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
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
  private final MailService mailService;


  @Value("${spring.application.base-url}")
  private final String appBaseUrl;;

  @Override
  @Retryable(
      retryFor = {OptimisticLockException.class, OptimisticLockingFailureException.class},
      maxAttempts = 3,
      backoff = @Backoff(delay = 100, multiplier = 2))
  public OrderResponseDto createOrder(Long userId, CreateOrderRequestDto orderDto) {
    try {
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

        if (!"ACTIVE".equals(food.getStatus())) {
          throw new IllegalArgumentException(
              "Food item is not available for order (status: "
                  + food.getStatus()
                  + ") for food ID: "
                  + itemDto.getFoodId());
        }

        Integer currentQuantity = food.getQuantity();
        if (currentQuantity == null) {
          currentQuantity = 0;
        }

        if (currentQuantity < quantity) {
          throw new IllegalArgumentException(
              "Insufficient stock for food ID: "
                  + itemDto.getFoodId()
                  + ". Available: "
                  + currentQuantity
                  + ", Requested: "
                  + quantity);
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

        food.setQuantity(currentQuantity - quantity);
        foodRepository.save(food);
      }

      order.setTotalAmount(totalAmount);
      Order savedOrder = orderRepository.save(order);

      log.info("Order created successfully with ID: {}", savedOrder.getId());
      mailService.sendSimpleMail(
          "A new order has been placed",
          "Order ID: "
              + savedOrder.getId()
              + " has been placed by user: "
              + user.getUsername()
              + ". Total amount: $"
              + totalAmount
              + ". Check at URL: "
              + appBaseUrl + "/admin/orders/" + savedOrder.getId());
      return orderMapper.toDto(savedOrder);
    } catch (OptimisticLockException | OptimisticLockingFailureException ex) {
      log.warn(
          "Inventory conflict detected for user {}. Item was modified by another transaction. Retrying...",
          userId);
      throw ex;
    } catch (Exception ex) {
      log.error("Error creating order for user ID: {}", userId, ex);
      throw new RuntimeException(
          "Unable to complete your order due to high demand. Please try again.", ex);
    }
  }

  @Override
  @Retryable(
      retryFor = {OptimisticLockException.class, OptimisticLockingFailureException.class},
      maxAttempts = 3,
      backoff = @Backoff(delay = 100, multiplier = 2))
  public OrderResponseDto createOrderWithPayment(Long userId, CheckoutRequestDto checkoutDto) {
    try {
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
              "Food item is not available for order (status: "
                  + food.getStatus()
                  + ") for food ID: "
                  + itemDto.getFoodId());
        }

        Integer currentQuantity = food.getQuantity();
        if (currentQuantity == null) {
          currentQuantity = 0;
        }

        if (currentQuantity < quantity) {
          throw new IllegalArgumentException(
              "Insufficient stock for food ID: "
                  + itemDto.getFoodId()
                  + ". Available: "
                  + currentQuantity
                  + ", Requested: "
                  + quantity);
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

        food.setQuantity(currentQuantity - quantity);
        foodRepository.save(food);
      }

      order.setTotalAmount(totalAmount);

      if ("card".equals(checkoutDto.getPaymentMethod())
          && checkoutDto.getPaymentIntentId() != null) {
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
        order.setPaymentStatus("pending");
        log.info("Cash on delivery order created");
      }

      Order savedOrder = orderRepository.save(order);
      log.info(
          "Order created successfully with ID: {} and payment method: {}",
          savedOrder.getId(),
          savedOrder.getPaymentMethod());

      mailService.sendSimpleMail(
          "A new order has been placed",
          "Order ID: "
              + savedOrder.getId()
              + " has been placed by user: "
              + user.getUsername()
              + ". Total amount: $"
              + totalAmount);
      return orderMapper.toDto(savedOrder);
    } catch (OptimisticLockException | OptimisticLockingFailureException ex) {
      log.warn(
          "Inventory conflict detected during checkout for user {}. Item was modified by another transaction. Retrying...",
          userId);
      throw ex;
    } catch (RuntimeException ex) {
      if (ex.getMessage().contains("Payment failed")) {
        throw ex;
      }
      log.error("Error creating order with payment for user ID: {}", userId, ex);
      throw new RuntimeException(
          "Unable to complete your order due to high demand. Please try again.", ex);
    } catch (Exception ex) {
      log.error("Unexpected error creating order with payment for user ID: {}", userId, ex);
      throw new RuntimeException(
          "Unable to process your order at this time. Please try again later.", ex);
    }
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

  @Override
  @Transactional(readOnly = true)
  public List<OrderResponseDto> getAllOrders() {
    log.info("Fetching all orders for admin");
    List<Order> orders = orderRepository.findAllByOrderByCreatedAtDesc();
    return orderMapper.toDtoList(orders);
  }

  @Override
  @Transactional(readOnly = true)
  public List<OrderResponseDto> getOrdersByStatus(OrderStatus status) {
    log.info("Fetching orders with status: {}", status);
    List<Order> orders = orderRepository.findByStatusOrderByCreatedAtDesc(status);
    return orderMapper.toDtoList(orders);
  }
}
