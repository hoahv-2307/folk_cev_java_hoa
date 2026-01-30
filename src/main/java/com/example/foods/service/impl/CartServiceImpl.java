package com.example.foods.service.impl;

import com.example.foods.dto.response.CartResponseDto;
import com.example.foods.entity.Cart;
import com.example.foods.entity.CartItem;
import com.example.foods.entity.Food;
import com.example.foods.entity.User;
import com.example.foods.mapper.CartMapper;
import com.example.foods.repository.CartRepository;
import com.example.foods.repository.FoodRepository;
import com.example.foods.repository.UserRepository;
import com.example.foods.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CartServiceImpl implements CartService {

  private final CartRepository cartRepository;
  private final UserRepository userRepository;
  private final FoodRepository foodRepository;
  private final CartMapper cartMapper;

  @Override
  public CartResponseDto getOrCreateCart(Long userId) {
    log.info("Getting or creating cart for user ID: {}", userId);
    Cart cart =
        cartRepository
            .findByUserId(userId)
            .orElseGet(
                () -> {
                  User user =
                      userRepository
                          .findById(userId)
                          .orElseThrow(
                              () ->
                                  new IllegalArgumentException(
                                      "User not found with ID: " + userId));
                  Cart newCart = Cart.builder().user(user).build();
                  return cartRepository.save(newCart);
                });
    return cartMapper.toDto(cart);
  }

  @Override
  public CartResponseDto addItemToCart(Long userId, Long foodId, Integer quantity) {
    log.info("Adding item to cart - User: {}, Food: {}, Quantity: {}", userId, foodId, quantity);

    Cart cart =
        cartRepository
            .findByUserId(userId)
            .orElseGet(
                () -> {
                  User user =
                      userRepository
                          .findById(userId)
                          .orElseThrow(
                              () ->
                                  new IllegalArgumentException(
                                      "User not found with ID: " + userId));
                  return cartRepository.save(Cart.builder().user(user).build());
                });

    Food food =
        foodRepository
            .findById(foodId)
            .orElseThrow(() -> new IllegalArgumentException("Food not found with ID: " + foodId));

    CartItem existingItem =
        cart.getItems().stream()
            .filter(item -> item.getFood().getId().equals(foodId))
            .findFirst()
            .orElse(null);

    if (existingItem != null) {
      existingItem.setQuantity(existingItem.getQuantity() + quantity);
    } else {
      CartItem newItem = CartItem.builder().cart(cart).food(food).quantity(quantity).build();
      cart.getItems().add(newItem);
    }

    Cart savedCart = cartRepository.save(cart);
    log.info("Item added to cart successfully");
    return cartMapper.toDto(savedCart);
  }

  @Override
  public CartResponseDto updateCartItemQuantity(Long userId, Long foodId, Integer quantity) {
    log.info(
        "Updating cart item quantity - User: {}, Food: {}, Quantity: {}", userId, foodId, quantity);

    Cart cart =
        cartRepository
            .findByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("Cart not found for user: " + userId));

    CartItem cartItem =
        cart.getItems().stream()
            .filter(item -> item.getFood().getId().equals(foodId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Item not found in cart"));

    if (quantity <= 0) {
      cart.getItems().remove(cartItem);
    } else {
      cartItem.setQuantity(quantity);
    }

    Cart savedCart = cartRepository.save(cart);
    return cartMapper.toDto(savedCart);
  }

  @Override
  public CartResponseDto removeItemFromCart(Long userId, Long foodId) {
    log.info("Removing item from cart - User: {}, Food: {}", userId, foodId);

    Cart cart =
        cartRepository
            .findByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("Cart not found for user: " + userId));

    cart.getItems().removeIf(item -> item.getFood().getId().equals(foodId));

    Cart savedCart = cartRepository.save(cart);
    log.info("Item removed from cart successfully");
    return cartMapper.toDto(savedCart);
  }

  @Override
  public void clearCart(Long userId) {
    log.info("Clearing cart for user ID: {}", userId);

    Cart cart =
        cartRepository
            .findByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("Cart not found for user: " + userId));

    cart.getItems().clear();
    cartRepository.save(cart);
    log.info("Cart cleared successfully");
  }
}
