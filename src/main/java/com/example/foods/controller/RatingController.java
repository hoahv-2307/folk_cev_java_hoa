package com.example.foods.controller;

import com.example.foods.dto.request.RatingRequestDto;
import com.example.foods.dto.response.UserResponseDto;
import com.example.foods.service.RatingService;
import com.example.foods.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/foods")
@RequiredArgsConstructor
@Slf4j
public class RatingController {

  private final RatingService ratingService;
  private final UserService userService;

  @PostMapping("/{id}/ratings")
  public ResponseEntity<Void> submitRating(
      @PathVariable("id") Long foodId,
      @Valid @RequestBody RatingRequestDto request,
      Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    String username = authentication.getName();
    UserResponseDto user = userService.getUserByUsername(username);
    log.info(
        "Submitting rating for food {} by user {}: {}", foodId, user.getId(), request.getScore());
    ratingService.submitRating(foodId, user.getId(), request.getScore(), request.getComment());
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @GetMapping("/{id}/ratings/me")
  public ResponseEntity<com.example.foods.dto.response.RatingResponseDto> getMyRating(
      @PathVariable("id") Long foodId, Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    UserResponseDto user = userService.getUserByUsername(authentication.getName());
    var dto = ratingService.getUserRating(foodId, user.getId());
    if (dto == null) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(dto);
  }
}
