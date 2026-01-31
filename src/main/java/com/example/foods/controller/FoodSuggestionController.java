package com.example.foods.controller;

import com.example.foods.dto.request.SuggestionRequestDto;
import com.example.foods.dto.response.SuggestionResponseDto;
import com.example.foods.service.FoodSuggestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class FoodSuggestionController {

  private final FoodSuggestionService suggestionService;

  @PostMapping("/api/food-suggestions")
  public ResponseEntity<SuggestionResponseDto> suggestFood(
      @Valid @ModelAttribute SuggestionRequestDto requestDto, Authentication authentication) {
    log.info("User {} submit suggestion: {}", authentication.getName(), requestDto.getName());
    var result = suggestionService.suggestFood(requestDto, authentication);
    return new ResponseEntity<>(result, HttpStatus.CREATED);
  }

  @PostMapping("/admin/food-suggestions/{id}/process")
  public ResponseEntity<SuggestionResponseDto> processSuggestion(
      @PathVariable Long id,
      @RequestParam String status,
      @RequestParam(required = false) String adminNote) {
    log.info("Admin processing suggestion {} -> {}", id, status);
    var result = suggestionService.processSuggestion(id, status, adminNote);
    return ResponseEntity.ok(result);
  }
}
