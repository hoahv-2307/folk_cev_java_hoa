package com.example.foods.service;

import com.example.foods.dto.request.UserRequestDto;
import com.example.foods.dto.response.UserResponseDto;
import java.util.List;

public interface UserService {
  UserResponseDto createUser(UserRequestDto userDto);

  UserResponseDto getUserById(Long id);

  List<UserResponseDto> getAllUsers();

  UserResponseDto updateUser(Long id, UserRequestDto userDto);

  void deleteUser(Long id);
}
