package com.example.foods.controller;

import com.example.foods.dto.request.CreateOrderRequestDto;
import com.example.foods.dto.response.OrderResponseDto;
import com.example.foods.entity.User;
import com.example.foods.repository.UserRepository;
import com.example.foods.service.OrderService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

  private final OrderService orderService;
  private final UserRepository userRepository;

  @PostMapping
  public ResponseEntity<OrderResponseDto> createOrder(
      @Valid @RequestBody CreateOrderRequestDto orderDto, Principal principal) {
    log.info("REST request to create order for user: {}", principal.getName());
    User user =
        userRepository
            .findByUsername(principal.getName())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
    OrderResponseDto createdOrder = orderService.createOrder(user.getId(), orderDto);
    return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<List<OrderResponseDto>> getUserOrders(Principal principal) {
    log.info("REST request to get orders for user: {}", principal.getName());
    User user =
        userRepository
            .findByUsername(principal.getName())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
    List<OrderResponseDto> orders = orderService.getUserOrders(user.getId());
    return ResponseEntity.ok(orders);
  }

  @GetMapping("/{id}")
  public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Long id) {
    log.info("REST request to get order with ID: {}", id);
    OrderResponseDto order = orderService.getOrderById(id);
    return ResponseEntity.ok(order);
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<OrderResponseDto> updateOrderStatus(
      @PathVariable Long id, @RequestParam String status) {
    log.info("REST request to update order {} status to: {}", id, status);
    OrderResponseDto updatedOrder = orderService.updateOrderStatus(id, status);
    return ResponseEntity.ok(updatedOrder);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> cancelOrder(@PathVariable Long id) {
    log.info("REST request to cancel order with ID: {}", id);
    orderService.cancelOrder(id);
    return ResponseEntity.noContent().build();
  }
}
