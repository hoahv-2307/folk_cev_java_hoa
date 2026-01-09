package com.example.foods.service;

import com.example.foods.dto.FoodDto;
import java.util.List;

/** Service interface for Food operations */
public interface FoodService {

  /** Create a new food item */
  FoodDto createFood(FoodDto foodDto);

  /** Get all food items */
  List<FoodDto> getAllFoods();

  /** Get food by ID */
  FoodDto getFoodById(Long id);

  /** Update existing food */
  FoodDto updateFood(Long id, FoodDto foodDto);

  /** Delete food by ID */
  void deleteFood(Long id);

  /** Get foods by category */
  List<FoodDto> getFoodsByCategory(String category);

  /** Search foods by name */
  List<FoodDto> searchFoodsByName(String name);

  /** Get foods by price range */
  List<FoodDto> getFoodsByPriceRange(Double minPrice, Double maxPrice);
}
