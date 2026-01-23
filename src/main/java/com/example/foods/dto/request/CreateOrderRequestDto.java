package com.example.foods.dto.request;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequestDto {
  @NotEmpty(message = "Order items cannot be empty")
  private List<OrderItemRequestDto> items;
}
