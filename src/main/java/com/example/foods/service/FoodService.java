package com.example.foods.service;

import com.example.foods.dto.FoodDto;
import java.util.List;

public interface FoodService {

  FoodDto createFood(FoodDto foodDto);

  List<FoodDto> getAllFoods();

  FoodDto getFoodById(Long id);

  FoodDto updateFood(Long id, FoodDto foodDto);

  void deleteFood(Long id);

  List<FoodDto> getFoodsByCategory(String category);

  List<FoodDto> searchFoodsByName(String name);

  List<FoodDto> getFoodsByPriceRange(Double minPrice, Double maxPrice);
}
