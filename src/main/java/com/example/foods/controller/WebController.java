package com.example.foods.controller;

import com.example.foods.dto.request.UserRequestDto;
import com.example.foods.dto.response.UserResponseDto;
import com.example.foods.service.FoodService;
import com.example.foods.service.UserService;
import jakarta.validation.Valid;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebController {

  private final FoodService foodService;
  private final UserService userService;

  @GetMapping("/")
  public String home() {
    return "redirect:/foods";
  }

  @GetMapping("/login")
  public String login(
      @RequestParam(value = "error", required = false) String error,
      @RequestParam(value = "logout", required = false) String logout,
      @RequestParam(value = "message", required = false) String message,
      Model model) {
    if (error != null) {
      if ("oauth2".equals(error) && message != null) {
        model.addAttribute("errorMessage", URLDecoder.decode(message, StandardCharsets.UTF_8));
      } else {
        model.addAttribute("errorMessage", "Invalid username or password!");
      }
    }
    if (logout != null) {
      model.addAttribute("message", "You have been logged out successfully.");
    }
    return "auth/login";
  }

  @GetMapping("/register")
  public String showRegisterForm(Model model) {
    model.addAttribute("user", new UserRequestDto());
    return "auth/register";
  }

  @PostMapping("/register")
  public String registerUser(
      @Valid @ModelAttribute("user") UserRequestDto userRequestDto,
      BindingResult bindingResult,
      RedirectAttributes redirectAttributes,
      Model model) {
    if (bindingResult.hasErrors()) {
      return "auth/register";
    }

    try {
      userRequestDto.setRole("USER");
      UserResponseDto createdUser = userService.createUser(userRequestDto);
      log.info("User registered successfully: {}", createdUser.getUsername());
      redirectAttributes.addFlashAttribute("message", "Registration successful! Please login.");
      return "redirect:/login";
    } catch (Exception e) {
      log.error("Registration failed: {}", e.getMessage());
      model.addAttribute("errorMessage", "Registration failed: " + e.getMessage());
      return "auth/register";
    }
  }

  @GetMapping("/foods")
  public String showFoodsPage(Model model, Principal principal, Authentication authentication) {
    model.addAttribute("foods", foodService.getAllFoods());
    model.addAttribute("username", principal.getName());

    boolean hasAdminRole =
        authentication.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

    return hasAdminRole ? "admin/foods" : "user/foods";
  }

  @GetMapping("/admin/foods")
  public String adminFoodsPage(Model model, Principal principal) {
    model.addAttribute("foods", foodService.getAllFoods());
    model.addAttribute("username", principal.getName());
    return "admin/foods";
  }
}
