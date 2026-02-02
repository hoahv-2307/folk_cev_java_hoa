package com.example.foods.service;

import com.example.foods.dto.response.FoodAnalyticsResponseDto;
import java.util.List;

public interface FoodAnalyticsService {

  void incrementViewCount(Long foodId);

  void incrementOrderCount(Long foodId, int quantity);

  List<FoodAnalyticsResponseDto> getAllFoodAnalytics();

  void syncRedisToDatabase();
}
