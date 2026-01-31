package com.example.foods.service;

import com.example.foods.dto.request.SuggestionRequestDto;
import com.example.foods.dto.response.SuggestionResponseDto;
import org.springframework.security.core.Authentication;

public interface FoodSuggestionService {
  SuggestionResponseDto suggestFood(SuggestionRequestDto requestDto, Authentication authentication);

  SuggestionResponseDto processSuggestion(Long id, String status, String adminNote);
}
