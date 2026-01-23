package com.example.foods.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {
  private Long id;
  private Long foodId;
  private String foodName;
  private Integer quantity;
  private Double price;
}
