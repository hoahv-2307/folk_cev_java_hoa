package com.example.foods.service;

import com.example.foods.dto.FoodDto;
import com.example.foods.entity.Food;
import com.example.foods.mapper.FoodMapper;
import com.example.foods.repository.FoodRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FoodServiceImplTest {
    
    @Mock
    private FoodRepository foodRepository;
    
    @Mock
    private FoodMapper foodMapper;
    
    @InjectMocks
    private FoodServiceImpl foodService;
    
    private FoodDto foodDto;
    private Food food;
    
    @BeforeEach
    void setUp() {
        foodDto = FoodDto.builder()
                .id(1L)
                .name("Test Pizza")
                .description("Test description")
                .category("Italian")
                .price(12.99)
                .build();
        
        food = Food.builder()
                .id(1L)
                .name("Test Pizza")
                .description("Test description")
                .category("Italian")
                .price(12.99)
                .build();
    }
    
    @Test
    void createFood_ShouldReturnCreatedFood_WhenValidInput() {
        // Given
        when(foodRepository.existsByNameIgnoreCase(anyString())).thenReturn(false);
        when(foodMapper.toEntity(any(FoodDto.class))).thenReturn(food);
        when(foodRepository.save(any(Food.class))).thenReturn(food);
        when(foodMapper.toDto(any(Food.class))).thenReturn(foodDto);
        
        // When
        FoodDto result = foodService.createFood(foodDto);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Pizza");
        verify(foodRepository).existsByNameIgnoreCase("Test Pizza");
        verify(foodRepository).save(any(Food.class));
    }
    
    @Test
    void createFood_ShouldThrowException_WhenFoodAlreadyExists() {
        // Given
        when(foodRepository.existsByNameIgnoreCase(anyString())).thenReturn(true);
        
        // When & Then
        assertThatThrownBy(() -> foodService.createFood(foodDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
        
        verify(foodRepository, never()).save(any(Food.class));
    }
    
    @Test
    void getFoodById_ShouldReturnFood_WhenFoodExists() {
        // Given
        when(foodRepository.findById(1L)).thenReturn(Optional.of(food));
        when(foodMapper.toDto(any(Food.class))).thenReturn(foodDto);
        
        // When
        FoodDto result = foodService.getFoodById(1L);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(foodRepository).findById(1L);
    }
    
    @Test
    void getFoodById_ShouldThrowException_WhenFoodNotFound() {
        // Given
        when(foodRepository.findById(1L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> foodService.getFoodById(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Food not found");
        
        verify(foodRepository).findById(1L);
    }
    
    @Test
    void getAllFoods_ShouldReturnAllFoods() {
        // Given
        List<Food> foods = Arrays.asList(food);
        List<FoodDto> foodDtos = Arrays.asList(foodDto);
        
        when(foodRepository.findAll()).thenReturn(foods);
        when(foodMapper.toDtoList(foods)).thenReturn(foodDtos);
        
        // When
        List<FoodDto> result = foodService.getAllFoods();
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Pizza");
        verify(foodRepository).findAll();
    }
    
    @Test
    void deleteFood_ShouldDeleteFood_WhenFoodExists() {
        // Given
        when(foodRepository.existsById(1L)).thenReturn(true);
        
        // When
        foodService.deleteFood(1L);
        
        // Then
        verify(foodRepository).existsById(1L);
        verify(foodRepository).deleteById(1L);
    }
    
    @Test
    void deleteFood_ShouldThrowException_WhenFoodNotFound() {
        // Given
        when(foodRepository.existsById(1L)).thenReturn(false);
        
        // When & Then
        assertThatThrownBy(() -> foodService.deleteFood(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Food not found");
        
        verify(foodRepository, never()).deleteById(any());
    }
}
