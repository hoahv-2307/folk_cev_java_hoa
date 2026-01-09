package com.example.foods.exception;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {
  private String message;
  private int status;
  private String error;
  private LocalDateTime timestamp;
  private String path;
}
