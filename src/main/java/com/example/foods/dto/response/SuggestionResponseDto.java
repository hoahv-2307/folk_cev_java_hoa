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
public class SuggestionResponseDto {
  private Long id;
  private Long userId;
  private String name;
  private String description;
  private Double price;
  private String category;
  private String imageUrl;
  private String status;
  private String adminNote;
  private LocalDateTime createdAt;
}
