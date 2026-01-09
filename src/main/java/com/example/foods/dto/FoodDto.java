package com.example.foods.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodDto {

  private Long id;

  @NotBlank(message = "Name is required")
  private String name;

  private String description;

  @NotBlank(message = "Category is required")
  private String category;

  @NotNull(message = "Price is required")
  @Positive(message = "Price must be positive")
  private Double price;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
