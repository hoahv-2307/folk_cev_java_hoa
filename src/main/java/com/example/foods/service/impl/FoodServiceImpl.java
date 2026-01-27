package com.example.foods.service.impl;

import com.example.foods.dto.request.FoodRequestDto;
import com.example.foods.dto.response.FoodResponseDto;
import com.example.foods.entity.Food;
import com.example.foods.mapper.FoodMapper;
import com.example.foods.repository.FoodRepository;
import com.example.foods.service.FileStorageService;
import com.example.foods.service.FoodService;
import java.util.ArrayList;
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
  private final FileStorageService fileStorageService;

  @Override
  public FoodResponseDto createFood(FoodRequestDto foodDto) {
    log.info("Creating new food: {}", foodDto.getName());
    if (foodRepository.existsByNameIgnoreCase(foodDto.getName())) {
      throw new IllegalArgumentException(
          "Food with name '" + foodDto.getName() + "' already exists");
    }
    Food food = foodMapper.toEntity(foodDto);
    log.info("Mapping food DTO to entity with images: {}", foodDto.getFoodImages());

    List<String> uploadedFileKeys = new ArrayList<>();
    try {
      if (foodDto.getFoodImages() != null) {
        foodDto
            .getFoodImages()
            .forEach(
                image -> {
                  String fileURL = fileStorageService.uploadFile(image);
                  uploadedFileKeys.add(fileURL);
                  food.addFoodImage(foodMapper.toFoodImageEntity(fileURL));
                  log.info("Uploaded and added image to food: {}", food.getFoodImages().size());
                });
      }
      log.info("Saving food entity to the database: {}", food.getName());
      Food savedFood = foodRepository.save(food);

      log.info("Successfully created food with ID: {}", savedFood.getId());
      return foodMapper.toDto(savedFood);
    } catch (Exception e) {
      log.error("Failed to create food, cleaning up uploaded files: {}", uploadedFileKeys);
      cleanupUploadedFiles(uploadedFileKeys);
      throw e;
    }
  }

  @Override
  @Transactional(readOnly = true)
  public List<FoodResponseDto> getAllFoods() {
    log.info("Retrieving all foods");
    List<Food> foods = foodRepository.findAll();
    return foodMapper.toDtoList(foods);
  }

  @Override
  @Transactional(readOnly = true)
  public FoodResponseDto getFoodById(Long id) {
    log.info("Retrieving food with ID: {}", id);
    Food food =
        foodRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Food not found with ID: " + id));
    return foodMapper.toDto(food);
  }

  @Override
  public FoodResponseDto updateFood(Long id, FoodRequestDto foodDto) {
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

    List<String> uploadedFileKeys = new ArrayList<>();
    try {
      if (foodDto.getFoodImages() != null && !foodDto.getFoodImages().isEmpty()) {
        log.info("Updating food images for food ID: {}", id);

        existingFood.getFoodImages().clear();

        foodDto
            .getFoodImages()
            .forEach(
                image -> {
                  String fileURL = fileStorageService.uploadFile(image);
                  uploadedFileKeys.add(fileURL);
                  existingFood.addFoodImage(foodMapper.toFoodImageEntity(fileURL));
                  log.info("Added new image to food: {}", fileURL);
                });

        log.info("Updated food images. New image count: {}", existingFood.getFoodImages().size());
      }

      Food updatedFood = foodRepository.save(existingFood);

      log.info("Successfully updated food with ID: {}", id);
      return foodMapper.toDto(updatedFood);
    } catch (Exception e) {
      log.error("Failed to update food, cleaning up uploaded files: {}", uploadedFileKeys);
      cleanupUploadedFiles(uploadedFileKeys);
      throw e;
    }
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
  public List<FoodResponseDto> getFoodsByCategory(String category) {
    log.info("Retrieving foods by category: {}", category);
    List<Food> foods = foodRepository.findByCategory(category);
    return foodMapper.toDtoList(foods);
  }

  @Override
  @Transactional(readOnly = true)
  public List<FoodResponseDto> searchFoodsByName(String name) {
    log.info("Searching foods by name: {}", name);
    List<Food> foods = foodRepository.findByNameContainingIgnoreCase(name);
    return foodMapper.toDtoList(foods);
  }

  @Override
  @Transactional(readOnly = true)
  public List<FoodResponseDto> getFoodsByPriceRange(Double minPrice, Double maxPrice) {
    log.info("Retrieving foods by price range: {} - {}", minPrice, maxPrice);

    if (minPrice < 0 || maxPrice < 0 || minPrice > maxPrice) {
      throw new IllegalArgumentException("Invalid price range");
    }

    List<Food> foods = foodRepository.findByPriceBetween(minPrice, maxPrice);
    return foodMapper.toDtoList(foods);
  }

  private void cleanupUploadedFiles(List<String> fileKeys) {
    if (fileKeys != null && !fileKeys.isEmpty()) {
      fileKeys.forEach(
          fileKey -> {
            try {
              fileStorageService.deleteFile(fileKey);
              log.info("Cleaned up orphaned file: {}", fileKey);
            } catch (Exception e) {
              log.warn("Failed to cleanup orphaned file: {}", fileKey, e);
            }
          });
    }
  }
}
