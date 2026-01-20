package com.example.foods.controller;

import com.example.foods.dto.FoodDto;
import com.example.foods.service.FoodService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/foods")
@RequiredArgsConstructor
@Slf4j
public class FoodController {

  private final FoodService foodService;

  @PostMapping
  public ResponseEntity<FoodDto> createFood(@Valid @ModelAttribute("food") FoodDto foodDto) {
    log.info("REST request to create food: {}", foodDto.getName());
    FoodDto createdFood = foodService.createFood(foodDto);
    return new ResponseEntity<>(createdFood, HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<List<FoodDto>> getAllFoods() {
    log.info("REST request to get all foods");
    List<FoodDto> foods = foodService.getAllFoods();
    return ResponseEntity.ok(foods);
  }

  @GetMapping("/{id}")
  public ResponseEntity<FoodDto> getFoodById(@PathVariable Long id) {
    log.info("REST request to get food with ID: {}", id);
    FoodDto food = foodService.getFoodById(id);
    return ResponseEntity.ok(food);
  }

  @PutMapping("/{id}")
  public ResponseEntity<FoodDto> updateFood(
      @PathVariable Long id, @Valid @ModelAttribute("food") FoodDto foodDto) {
    log.info("REST request to update food with ID: {}", id);
    FoodDto updatedFood = foodService.updateFood(id, foodDto);
    return ResponseEntity.ok(updatedFood);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteFood(@PathVariable Long id) {
    log.info("REST request to delete food with ID: {}", id);
    foodService.deleteFood(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/category/{category}")
  public ResponseEntity<List<FoodDto>> getFoodsByCategory(@PathVariable String category) {
    log.info("REST request to get foods by category: {}", category);
    List<FoodDto> foods = foodService.getFoodsByCategory(category);
    return ResponseEntity.ok(foods);
  }

  @GetMapping("/search")
  public ResponseEntity<List<FoodDto>> searchFoodsByName(@RequestParam String name) {
    log.info("REST request to search foods by name: {}", name);
    List<FoodDto> foods = foodService.searchFoodsByName(name);
    return ResponseEntity.ok(foods);
  }

  @GetMapping("/price-range")
  public ResponseEntity<List<FoodDto>> getFoodsByPriceRange(
      @RequestParam Double minPrice, @RequestParam Double maxPrice) {
    log.info("REST request to get foods by price range: {} - {}", minPrice, maxPrice);
    List<FoodDto> foods = foodService.getFoodsByPriceRange(minPrice, maxPrice);
    return ResponseEntity.ok(foods);
  }
}
