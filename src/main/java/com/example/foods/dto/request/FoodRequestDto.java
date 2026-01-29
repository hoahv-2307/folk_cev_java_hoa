package com.example.foods.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodRequestDto {

  private Long id;

  @NotBlank(message = "Name is required")
  private String name;

  private String description;

  @NotBlank(message = "Category is required")
  private String category;

  @NotNull(message = "Price is required")
  @Positive(message = "Price must be positive")
  private Double price;

  @NotNull(message = "Quantity is required")
  @PositiveOrZero(message = "Quantity must be positive or zero")
  private Integer quantity;

  @NotBlank(message = "Status is required")
  private String status;

  private List<MultipartFile> foodImages;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
