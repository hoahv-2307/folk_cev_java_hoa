package com.example.foods.mapper;

import com.example.foods.dto.request.FoodRequestDto;
import com.example.foods.dto.response.FoodImageResponseDto;
import com.example.foods.dto.response.FoodResponseDto;
import com.example.foods.entity.Food;
import com.example.foods.entity.FoodImage;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FoodMapper {

  @Mapping(target = "averageRating", ignore = true)
  @Mapping(target = "ratingCount", ignore = true)
  FoodResponseDto toDto(Food food);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "foodImages", ignore = true)
  @Mapping(target = "version", ignore = true)
  @Mapping(target = "viewCount", ignore = true)
  @Mapping(target = "orderCount", ignore = true)
  Food toEntity(FoodRequestDto foodDto);

  List<FoodResponseDto> toDtoList(List<Food> foods);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "foodImages", ignore = true)
  @Mapping(target = "version", ignore = true)
  @Mapping(target = "viewCount", ignore = true)
  @Mapping(target = "orderCount", ignore = true)
  void updateEntityFromDto(FoodRequestDto foodDto, @MappingTarget Food food);

  FoodImageResponseDto toFoodImageDto(FoodImage foodImage);

  List<FoodImageResponseDto> toFoodImageDtoList(List<FoodImage> foodImages);

  default FoodImage toFoodImageEntity(String imageUrl) {
    if (imageUrl == null) {
      return null;
    }
    return FoodImage.builder().imageUrl(imageUrl).build();
  }
}
