package com.example.foods.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentIntentResponseDto {
  private String clientSecret;
  private String paymentIntentId;
  private Long amount;
  private String currency;
  private String status;
}
