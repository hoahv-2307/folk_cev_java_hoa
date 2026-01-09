package com.example.foods.mapper;

import com.example.foods.dto.request.UserRequestDto;
import com.example.foods.dto.response.UserResponseDto;
import com.example.foods.entity.User;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
  UserResponseDto toDto(User user);

  @Mapping(target = "id", ignore = true)
  User toEntity(UserRequestDto userDto);

  List<UserResponseDto> toDtoList(List<User> users);

  @Mapping(target = "authorities", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "password", ignore = true)
  void updateEntityFromDto(UserRequestDto userDto, @MappingTarget User user);
}
