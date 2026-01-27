package com.example.foods.dto.response;

import com.example.foods.entity.FoodImage;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodResponseDto {
  private Long id;
  private String name;
  private String description;
  private String category;
  private Double price;
  private List<FoodImage> foodImages;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
