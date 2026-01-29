package com.example.foods.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {
  private Long id;
  private Long foodId;
  private String foodName;
  private Double foodPrice;
  private Integer quantity;
  private Double subtotal;
  private List<String> foodImageUrls;
}
