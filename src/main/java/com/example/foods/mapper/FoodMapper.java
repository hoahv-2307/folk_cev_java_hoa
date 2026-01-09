package com.example.foods.mapper;

import com.example.foods.dto.FoodDto;
import com.example.foods.entity.Food;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface FoodMapper {
    
    /**
     * Convert Food entity to FoodDto
     */
    FoodDto toDto(Food food);
    
    /**
     * Convert FoodDto to Food entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Food toEntity(FoodDto foodDto);
    
    /**
     * Convert list of Food entities to list of FoodDtos
     */
    List<FoodDto> toDtoList(List<Food> foods);
    
    /**
     * Update existing Food entity with data from FoodDto
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(FoodDto foodDto, @MappingTarget Food food);
}
