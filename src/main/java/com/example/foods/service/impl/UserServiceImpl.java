package com.example.foods.service.impl;

import com.example.foods.dto.request.UpdateProfileRequestDto;
import com.example.foods.dto.request.UserRequestDto;
import com.example.foods.dto.response.UserResponseDto;
import com.example.foods.entity.User;
import com.example.foods.mapper.UserMapper;
import com.example.foods.repository.UserRepository;
import com.example.foods.service.UserService;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {
  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;

  @Override
  public UserDetails loadUserByUsername(String username) {
    User user = userRepository.findByUsername(username).orElse(null);
    if (user == null) {
      log.warn("User with username: {} not found", username);
      throw new IllegalArgumentException("User not found with username: " + username);
    }
    return user;
  }

  @Override
  public UserResponseDto createUser(UserRequestDto userDto) {
    if (userRepository.existsByUsername(userDto.getUsername())) {
      throw new IllegalArgumentException("Username already exists: " + userDto.getUsername());
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
    User user =
        userRepository
            .findById(id)
            .orElseThrow(
                () -> {
                  log.warn("User with ID: {} not found", id);
                  return new UsernameNotFoundException("User not found with ID: " + id);
                });
    return userMapper.toDto(user);
  }

  @Override
  public UserResponseDto getUserByUsername(String username) {
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(
                () -> {
                  log.warn("User with username: {} not found", username);
                  return new IllegalArgumentException("User not found with username: " + username);
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
    User user =
        userRepository
            .findById(id)
            .orElseThrow(
                () -> {
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
    userRepository
        .findById(id)
        .orElseThrow(
            () -> {
              log.warn("User with ID: {} not found for deletion", id);
              return new IllegalArgumentException("User not found with ID: " + id);
            });
    userRepository.deleteById(id);
    log.info("Deleted user with ID: {}", id);
  }

  @Override
  @Transactional()
  public UserResponseDto updateProfile(Long id, UpdateProfileRequestDto profileDto) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(
                () -> {
                  log.warn("User with ID: {} not found for profile update", id);
                  throw new IllegalArgumentException("User not found with ID: " + id);
                });

    if (profileDto.getUsername() != null && !profileDto.getUsername().isBlank()) {
      if (!user.getUsername().equals(profileDto.getUsername())
          && userRepository.existsByUsername(profileDto.getUsername())) {
        throw new IllegalArgumentException("Username already exists: " + profileDto.getUsername());
      }
      user.setUsername(profileDto.getUsername());
    }

    if (profileDto.getEmail() != null && !profileDto.getEmail().isBlank()) {
      if (!user.getEmail().equals(profileDto.getEmail())
          && userRepository.existsByEmail(profileDto.getEmail())) {
        throw new IllegalArgumentException("Email already exists: " + profileDto.getEmail());
      }
      user.setEmail(profileDto.getEmail());
    }

    if (profileDto.getPassword() != null && !profileDto.getPassword().isBlank()) {
      user.setPassword(passwordEncoder.encode(profileDto.getPassword()));
    }

    user = userRepository.save(user);
    log.info("Updated profile for user with ID: {}", user.getId());
    return userMapper.toDto(user);
  }
}
