package com.example.foods.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {
  private Long id;

  @NotBlank(message = "Username is required")
  private String username;

  @NotBlank(message = "Email is required")
  private String email;

  @NotBlank(message = "Role is required")
  private String role;

  @NotBlank(message = "Password is required")
  private String password;
}
