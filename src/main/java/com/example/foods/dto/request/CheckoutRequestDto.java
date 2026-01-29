package com.example.foods.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
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
  @Pattern(regexp = "card|cash", message = "Payment method must be either 'card' or 'cash'")
  private String paymentMethod;

  private String paymentIntentId;
}
