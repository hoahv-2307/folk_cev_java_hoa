package com.example.foods.service;

import com.example.foods.dto.request.FoodRequestDto;
import com.example.foods.dto.response.FoodResponseDto;
import java.util.List;

public interface FoodService {

  FoodResponseDto createFood(FoodRequestDto foodDto);

  List<FoodResponseDto> getAllFoods();

  FoodResponseDto getFoodById(Long id);

  FoodResponseDto updateFood(Long id, FoodRequestDto foodDto);

  void deleteFood(Long id);

  List<FoodResponseDto> getFoodsByCategory(String category);

  List<FoodResponseDto> searchFoodsByName(String name);

  List<FoodResponseDto> getFoodsByPriceRange(Double minPrice, Double maxPrice);
}
