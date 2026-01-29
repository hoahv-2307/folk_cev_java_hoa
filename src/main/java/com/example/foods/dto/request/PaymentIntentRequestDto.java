package com.example.foods.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentIntentRequestDto {

  @NotNull(message = "Amount is required")
  @Positive(message = "Amount must be positive")
  private Long amount; // Amount in cents

  @NotBlank(message = "Currency is required")
  @Builder.Default
  private String currency = "usd";

  private String description;

  private String receiptEmail;
}
