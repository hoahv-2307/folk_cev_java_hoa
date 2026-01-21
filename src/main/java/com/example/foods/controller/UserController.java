package com.example.foods.controller;

import com.example.foods.dto.request.UpdateProfileRequestDto;
import com.example.foods.dto.response.UserResponseDto;
import com.example.foods.entity.User;
import com.example.foods.repository.UserRepository;
import com.example.foods.service.UserService;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

  private final UserService userService;
  private final UserRepository userRepository;

  @GetMapping("/me")
  public ResponseEntity<UserResponseDto> getCurrentUser(Principal principal) {
    log.info("REST request to get current user: {}", principal.getName());
    User user =
        userRepository
            .findByUsername(principal.getName())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
    UserResponseDto userDto = userService.getUserById(user.getId());
    return ResponseEntity.ok(userDto);
  }

  @PutMapping("/profile")
  public ResponseEntity<UserResponseDto> updateProfile(
      @Valid @RequestBody UpdateProfileRequestDto profileDto, Authentication authentication) {
    log.info("REST request to update profile for user: {}", authentication.getName());

    User user =
        userRepository
            .findByUsername(authentication.getName())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

    UserResponseDto updatedUser = userService.updateProfile(user.getId(), profileDto);
    return ResponseEntity.ok(updatedUser);
  }
}
