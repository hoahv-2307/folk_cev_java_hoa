package com.example.foods.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequestDto {
  private String username;

  @Email(message = "Email is not valid")
  private String email;

  @Size(min = 6, message = "Password must be at least 6 characters")
  private String password;
}
