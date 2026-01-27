package com.example.foods.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodImageResponseDto {
  private Long id;
  private String imageUrl;
  private LocalDateTime createdAt;
}
