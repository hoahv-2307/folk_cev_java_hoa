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
public class OrderResponseDto {
  private Long id;
  private Long userId;
  private String username;
  private List<OrderItemDto> items;
  private Double totalAmount;
  private String status;
  private String paymentMethod;
  private String paymentIntentId;
  private String paymentStatus;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
