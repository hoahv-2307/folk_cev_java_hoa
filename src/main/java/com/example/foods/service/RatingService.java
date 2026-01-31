package com.example.foods.service;

public interface RatingService {

  void submitRating(Long foodId, Long userId, Integer score, String comment);

  Double getAverageRating(Long foodId);

  Long getRatingCount(Long foodId);

  com.example.foods.dto.response.RatingResponseDto getUserRating(Long foodId, Long userId);
}
