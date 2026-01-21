package com.example.foods.dto.response;

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
public class CartResponseDto {
  private Long id;
  private Long userId;
  private List<CartItemDto> items;
  private Double totalAmount;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
