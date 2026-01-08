package com.example.foods.service.impl;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.foods.dto.request.UserRequestDto;
import com.example.foods.dto.response.UserResponseDto;
import com.example.foods.entity.User;
import com.example.foods.mapper.UserMapper;
import com.example.foods.repository.UserRepository;
import com.example.foods.service.UserService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {
  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;

  @Override
  public UserDetails loadUserByUsername(String username) {
    User user = userRepository.findByUsername(username);
    if (user == null) {
      log.warn("User with username: {} not found", username);
      throw new IllegalArgumentException("User not found with username: " + username);
    }
    return user;
  }

  @Override
  public UserResponseDto createUser(UserRequestDto userDto) {
    if (userRepository.existsByUsername(userDto.getUsername())) {
      throw new UsernameNotFoundException("Username already exists: " + userDto.getUsername());
    }
    
    if (userRepository.existsByEmail(userDto.getEmail())) {
      throw new IllegalArgumentException("Email already exists: " + userDto.getEmail());
    }
    
    User user = userMapper.toEntity(userDto);
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    user = userRepository.save(user);
    log.info("Created new user with ID: {}", user.getId());
    return userMapper.toDto(user);
  }

  @Override
  public UserResponseDto getUserById(Long id) {
    User user = userRepository.findById(id).orElseThrow(() -> {
      log.warn("User with ID: {} not found", id);
      return new UsernameNotFoundException("User not found with ID: " + id);
    });
    return userMapper.toDto(user);
  }

  @Override
  public java.util.List<UserResponseDto> getAllUsers() {
    List<User> users = userRepository.findAll();
    return userMapper.toDtoList(users);
  }

  @Override
  public UserResponseDto updateUser(Long id, UserRequestDto userDto) {
    User user = userRepository.findById(id).orElseThrow(() -> {
      log.warn("User with ID: {} not found for update", id);
      throw new IllegalArgumentException("User not found with ID: " + id);
    });
    userMapper.updateEntityFromDto(userDto, user);
    if (userDto.getPassword() != null && !userDto.getPassword().isBlank()) {
      user.setPassword(passwordEncoder.encode(userDto.getPassword()));
    }
    user = userRepository.save(user);
    log.info("Updated user with ID: {}", user.getId());
    return userMapper.toDto(user);
  }

  @Override
  public void deleteUser(Long id) {
    userRepository.deleteById(id);
    log.info("Deleted user with ID: {}", id);
  }
}
