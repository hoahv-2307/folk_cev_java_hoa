package com.example.foods.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodAnalyticsResponseDto {
  private Long id;
  private String name;
  private String category;
  private Double price;
  private Long viewCount;
  private Long orderCount;
}
