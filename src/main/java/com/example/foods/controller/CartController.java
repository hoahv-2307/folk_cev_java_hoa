package com.example.foods.controller;

import com.example.foods.dto.response.CartResponseDto;
import com.example.foods.dto.response.UserResponseDto;
import com.example.foods.service.CartService;
import com.example.foods.service.UserService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {

  private final CartService cartService;
  private final UserService userService;

  @GetMapping
  public ResponseEntity<CartResponseDto> getCart(Principal principal) {
    log.info("REST request to get cart for user: {}", principal.getName());
    UserResponseDto user = userService.getUserByUsername(principal.getName());
    CartResponseDto cart = cartService.getOrCreateCart(user.getId());
    return ResponseEntity.ok(cart);
  }

  @PostMapping("/items")
  public ResponseEntity<CartResponseDto> addItemToCart(
      @RequestParam Long foodId, @RequestParam Integer quantity, Principal principal) {
    log.info(
        "REST request to add item to cart - User: {}, Food: {}, Quantity: {}",
        principal.getName(),
        foodId,
        quantity);
    UserResponseDto user = userService.getUserByUsername(principal.getName());
    CartResponseDto cart = cartService.addItemToCart(user.getId(), foodId, quantity);
    return ResponseEntity.ok(cart);
  }

  @PutMapping("/items/{foodId}")
  public ResponseEntity<CartResponseDto> updateCartItemQuantity(
      @PathVariable Long foodId, @RequestParam Integer quantity, Principal principal) {
    log.info(
        "REST request to update cart item - User: {}, Food: {}, Quantity: {}",
        principal.getName(),
        foodId,
        quantity);
    UserResponseDto user = userService.getUserByUsername(principal.getName());
    CartResponseDto cart = cartService.updateCartItemQuantity(user.getId(), foodId, quantity);
    return ResponseEntity.ok(cart);
  }

  @DeleteMapping("/items/{foodId}")
  public ResponseEntity<CartResponseDto> removeItemFromCart(
      @PathVariable Long foodId, Principal principal) {
    log.info(
        "REST request to remove item from cart - User: {}, Food: {}", principal.getName(), foodId);
    UserResponseDto user = userService.getUserByUsername(principal.getName());
    CartResponseDto cart = cartService.removeItemFromCart(user.getId(), foodId);
    return ResponseEntity.ok(cart);
  }

  @DeleteMapping
  public ResponseEntity<Void> clearCart(Principal principal) {
    log.info("REST request to clear cart for user: {}", principal.getName());
    UserResponseDto user = userService.getUserByUsername(principal.getName());
    cartService.clearCart(user.getId());
    return ResponseEntity.noContent().build();
  }
}
