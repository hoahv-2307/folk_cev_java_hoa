package com.example.foods.controller;

import com.example.foods.dto.request.UpdateProfileRequestDto;
import com.example.foods.dto.response.UserResponseDto;
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

  @GetMapping("/me")
  public ResponseEntity<UserResponseDto> getCurrentUser(Principal principal) {
    log.info("REST request to get current user: {}", principal.getName());
    UserResponseDto userDto = userService.getUserByUsername(principal.getName());
    return ResponseEntity.ok(userDto);
  }

  @PutMapping("/profile")
  public ResponseEntity<UserResponseDto> updateProfile(
      @Valid @RequestBody UpdateProfileRequestDto profileDto, Authentication authentication) {
    log.info("REST request to update profile for user: {}", authentication.getName());

    UserResponseDto user = userService.getUserByUsername(authentication.getName());

    UserResponseDto updatedUser = userService.updateProfile(user.getId(), profileDto);
    return ResponseEntity.ok(updatedUser);
  }
}
