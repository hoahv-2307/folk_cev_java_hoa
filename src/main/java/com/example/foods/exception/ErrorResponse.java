package com.example.foods.exception;

import lombok.Data;
import lombok.Builder;
import java.time.LocalDateTime;

@Data
@Builder
public class ErrorResponse {
    private String message;
    private int status;
    private String error;
    private LocalDateTime timestamp;
    private String path;
}
