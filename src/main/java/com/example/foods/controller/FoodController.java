package com.example.foods.controller;

import com.example.foods.dto.request.FoodRequestDto;
import com.example.foods.dto.response.FoodResponseDto;
import com.example.foods.service.FileStorageService;
import com.example.foods.service.FoodService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/foods")
@RequiredArgsConstructor
@Slf4j
public class FoodController {

  private final FoodService foodService;
  private final FileStorageService fileStorageService;

  @PostMapping
  public ResponseEntity<FoodResponseDto> createFood(
      @Valid @ModelAttribute("food") FoodRequestDto foodDto) {
    log.info("REST request to create food: {}", foodDto.getName());
    FoodResponseDto createdFood = foodService.createFood(foodDto);
    return new ResponseEntity<>(createdFood, HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<List<FoodResponseDto>> getAllFoods() {
    log.info("REST request to get all foods");
    List<FoodResponseDto> foods = foodService.getAllFoods();
    return ResponseEntity.ok(foods);
  }

  @GetMapping("/{id}")
  public ResponseEntity<FoodResponseDto> getFoodById(@PathVariable Long id) {
    log.info("REST request to get food with ID: {}", id);
    FoodResponseDto food = foodService.getFoodById(id);
    return ResponseEntity.ok(food);
  }

  @PutMapping("/{id}")
  public ResponseEntity<FoodResponseDto> updateFood(
      @PathVariable Long id, @Valid @ModelAttribute("food") FoodRequestDto foodDto) {
    log.info("REST request to update food with ID: {}", id);
    FoodResponseDto updatedFood = foodService.updateFood(id, foodDto);
    return ResponseEntity.ok(updatedFood);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteFood(@PathVariable Long id) {
    log.info("REST request to delete food with ID: {}", id);
    foodService.deleteFood(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/category/{category}")
  public ResponseEntity<List<FoodResponseDto>> getFoodsByCategory(@PathVariable String category) {
    log.info("REST request to get foods by category: {}", category);
    List<FoodResponseDto> foods = foodService.getFoodsByCategory(category);
    return ResponseEntity.ok(foods);
  }

  @GetMapping("/search")
  public ResponseEntity<List<FoodResponseDto>> searchFoodsByName(@RequestParam String name) {
    log.info("REST request to search foods by name: {}", name);
    List<FoodResponseDto> foods = foodService.searchFoodsByName(name);
    return ResponseEntity.ok(foods);
  }

  @GetMapping("/price-range")
  public ResponseEntity<List<FoodResponseDto>> getFoodsByPriceRange(
      @RequestParam Double minPrice, @RequestParam Double maxPrice) {
    log.info("REST request to get foods by price range: {} - {}", minPrice, maxPrice);
    List<FoodResponseDto> foods = foodService.getFoodsByPriceRange(minPrice, maxPrice);
    return ResponseEntity.ok(foods);
  }

  @GetMapping("/images/{filename}")
  public ResponseEntity<byte[]> getImage(@PathVariable String filename) {
    log.info("REST request to get image: {}", filename);
    try {
      byte[] imageBytes = fileStorageService.downloadFile(filename);
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.IMAGE_JPEG);
      return ResponseEntity.ok().headers(headers).body(imageBytes);
    } catch (Exception e) {
      log.error("Error retrieving image: {}", filename, e);
      return ResponseEntity.notFound().build();
    }
  }
}
