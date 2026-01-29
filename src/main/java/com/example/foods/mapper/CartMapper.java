package com.example.foods.mapper;

import com.example.foods.dto.response.CartItemDto;
import com.example.foods.dto.response.CartResponseDto;
import com.example.foods.entity.Cart;
import com.example.foods.entity.CartItem;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CartMapper {

  @Mapping(target = "userId", source = "user.id")
  @Mapping(target = "totalAmount", expression = "java(calculateTotalAmount(cart))")
  CartResponseDto toDto(Cart cart);

  @Mapping(target = "foodId", source = "food.id")
  @Mapping(target = "foodName", source = "food.name")
  @Mapping(target = "foodPrice", source = "food.price")
  @Mapping(
      target = "foodImageUrls",
      expression =
          "java(cartItem.getFood().getFoodImages().stream().map(img -> img.getImageUrl()).toList())")
  @Mapping(target = "subtotal", expression = "java(calculateSubtotal(cartItem))")
  CartItemDto toItemDto(CartItem cartItem);

  List<CartItemDto> toItemDtoList(List<CartItem> cartItems);

  default Double calculateTotalAmount(Cart cart) {
    return cart.getItems().stream()
        .mapToDouble(item -> item.getFood().getPrice() * item.getQuantity())
        .sum();
  }

  default Double calculateSubtotal(CartItem cartItem) {
    return cartItem.getFood().getPrice() * cartItem.getQuantity();
  }
}
