package com.example.foods.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequestDto {

  @NotEmpty(message = "Order items cannot be empty")
  private List<OrderItemRequestDto> items;

  @NotBlank(message = "Payment method is required")
  private String paymentMethod; // "cash" or "card"

  private String paymentIntentId; // For card payments
}
