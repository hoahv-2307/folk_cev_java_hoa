package com.example.foods.service;

import com.example.foods.dto.response.CartResponseDto;

public interface CartService {
  CartResponseDto getOrCreateCart(Long userId);

  CartResponseDto addItemToCart(Long userId, Long foodId, Integer quantity);

  CartResponseDto updateCartItemQuantity(Long userId, Long foodId, Integer quantity);

  CartResponseDto removeItemFromCart(Long userId, Long foodId);

  void clearCart(Long userId);
}
