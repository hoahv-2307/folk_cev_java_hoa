package com.example.foods.mapper;

import com.example.foods.dto.response.OrderItemDto;
import com.example.foods.dto.response.OrderResponseDto;
import com.example.foods.entity.Order;
import com.example.foods.entity.OrderItem;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderMapper {

  @Mapping(target = "userId", source = "user.id")
  @Mapping(target = "username", source = "user.username")
  OrderResponseDto toDto(Order order);

  List<OrderResponseDto> toDtoList(List<Order> orders);

  @Mapping(target = "foodId", source = "food.id")
  @Mapping(target = "foodName", source = "food.name")
  OrderItemDto toItemDto(OrderItem orderItem);

  List<OrderItemDto> toItemDtoList(List<OrderItem> orderItems);
}
