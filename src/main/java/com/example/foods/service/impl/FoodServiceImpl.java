package com.example.foods.service.impl;

import com.example.foods.dto.FoodDto;
import com.example.foods.entity.Food;
import com.example.foods.mapper.FoodMapper;
import com.example.foods.repository.FoodRepository;
import com.example.foods.service.FoodService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FoodServiceImpl implements FoodService {

  private final FoodRepository foodRepository;
  private final FoodMapper foodMapper;

  @Override
  public FoodDto createFood(FoodDto foodDto) {
    log.info("Creating new food: {}", foodDto.getName());

    // Check if food with same name already exists
    if (foodRepository.existsByNameIgnoreCase(foodDto.getName())) {
      throw new IllegalArgumentException(
          "Food with name '" + foodDto.getName() + "' already exists");
    }

    Food food = foodMapper.toEntity(foodDto);
    Food savedFood = foodRepository.save(food);

    log.info("Successfully created food with ID: {}", savedFood.getId());
    return foodMapper.toDto(savedFood);
  }

  @Override
  @Transactional(readOnly = true)
  public List<FoodDto> getAllFoods() {
    log.info("Retrieving all foods");
    List<Food> foods = foodRepository.findAll();
    return foodMapper.toDtoList(foods);
  }

  @Override
  @Transactional(readOnly = true)
  public FoodDto getFoodById(Long id) {
    log.info("Retrieving food with ID: {}", id);
    Food food =
        foodRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Food not found with ID: " + id));
    return foodMapper.toDto(food);
  }

  @Override
  public FoodDto updateFood(Long id, FoodDto foodDto) {
    log.info("Updating food with ID: {}", id);

    Food existingFood =
        foodRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Food not found with ID: " + id));

    if (!existingFood.getName().equalsIgnoreCase(foodDto.getName())
        && foodRepository.existsByNameIgnoreCase(foodDto.getName())) {
      throw new IllegalArgumentException(
          "Food with name '" + foodDto.getName() + "' already exists");
    }

    foodMapper.updateEntityFromDto(foodDto, existingFood);
    Food updatedFood = foodRepository.save(existingFood);

    log.info("Successfully updated food with ID: {}", id);
    return foodMapper.toDto(updatedFood);
  }

  @Override
  public void deleteFood(Long id) {
    log.info("Deleting food with ID: {}", id);

    if (!foodRepository.existsById(id)) {
      throw new IllegalArgumentException("Food not found with ID: " + id);
    }

    foodRepository.deleteById(id);
    log.info("Successfully deleted food with ID: {}", id);
  }

  @Override
  @Transactional(readOnly = true)
  public List<FoodDto> getFoodsByCategory(String category) {
    log.info("Retrieving foods by category: {}", category);
    List<Food> foods = foodRepository.findByCategory(category);
    return foodMapper.toDtoList(foods);
  }

  @Override
  @Transactional(readOnly = true)
  public List<FoodDto> searchFoodsByName(String name) {
    log.info("Searching foods by name: {}", name);
    List<Food> foods = foodRepository.findByNameContainingIgnoreCase(name);
    return foodMapper.toDtoList(foods);
  }

  @Override
  @Transactional(readOnly = true)
  public List<FoodDto> getFoodsByPriceRange(Double minPrice, Double maxPrice) {
    log.info("Retrieving foods by price range: {} - {}", minPrice, maxPrice);

    if (minPrice < 0 || maxPrice < 0 || minPrice > maxPrice) {
      throw new IllegalArgumentException("Invalid price range");
    }

    List<Food> foods = foodRepository.findByPriceBetween(minPrice, maxPrice);
    return foodMapper.toDtoList(foods);
  }
}
