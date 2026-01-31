package com.example.foods.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuggestionRequestDto {

  @NotBlank(message = "Name is required")
  private String name;

  private String description;

  @NotBlank(message = "Category is required")
  private String category;

  @NotNull(message = "Price is required")
  @Positive(message = "Price must be positive")
  private Double price;

  private MultipartFile image;
}
