package com.example.foods.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsUpdateDto {
  private Long foodId;
  private Long viewCount;
  private Long orderCount;
}
