package com.example.foods.controller;

import com.example.foods.service.CustomOAuth2User;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

  @GetMapping("/user-info")
  public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal OAuth2User principal) {
    if (principal == null) {
      return ResponseEntity.status(401).body("User not authenticated");
    }

    log.info("Current authenticated user: {}", principal.getName());

    if (principal instanceof CustomOAuth2User customUser) {
      return ResponseEntity.ok()
          .body(
              Map.of(
                  "id", customUser.getId(),
                  "username", customUser.getName(),
                  "email", customUser.getEmail(),
                  "role", customUser.getRole()));
    }

    return ResponseEntity.ok()
        .body(
            Map.of(
                "username", principal.getName(),
                "attributes", principal.getAttributes()));
  }
}
