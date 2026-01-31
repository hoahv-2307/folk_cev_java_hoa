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
public class RatingResponseDto {
  private Long id;
  private Long foodId;
  private Long userId;
  private Integer score;
  private String comment;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
