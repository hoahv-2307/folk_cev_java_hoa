package com.example.foods.controller;

import com.example.foods.constant.OrderStatus;
import com.example.foods.dto.request.CheckoutRequestDto;
import com.example.foods.dto.request.OrderItemRequestDto;
import com.example.foods.dto.request.UserRequestDto;
import com.example.foods.dto.response.UserResponseDto;
import com.example.foods.service.CartService;
import com.example.foods.service.FoodService;
import com.example.foods.service.OrderService;
import com.example.foods.service.PaymentService;
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
  private final OrderService orderService;
  private final CartService cartService;
  private final PaymentService paymentService;

  @GetMapping("/")
  public String home() {
    return "redirect:/foods";
  }

  @GetMapping("/error")
  public String error() {
    return "error";
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

  @GetMapping("/admin/foods/edit/{id}")
  public String showEditFoodPage(@PathVariable Long id, Model model) {
    model.addAttribute("food", foodService.getFoodById(id));
    log.info("Navigating to edit food page for food ID: {}", id);
    return "fragments/admin/admin-add-food :: add-food-modal";
  }

  @GetMapping("/admin/foods/create")
  public String showCreateFoodPage(Model model) {
    model.addAttribute("food", null);
    log.info("Navigating to create food page");
    return "fragments/admin/admin-add-food :: add-food-modal";
  }

  @GetMapping("/profile")
  public String showProfilePage(Model model, Authentication authentication) {
    log.info("Navigating to profile page for user: {}", authentication.getName());

    UserResponseDto user = userService.getUserByUsername(authentication.getName());

    var orders = orderService.getUserOrders(user.getId());
    var cart = cartService.getOrCreateCart(user.getId());

    model.addAttribute("username", user.getUsername());
    model.addAttribute("userEmail", user.getEmail());
    model.addAttribute("userId", user.getId());
    model.addAttribute("totalOrders", orders.size());
    model.addAttribute("orders", orders);
    model.addAttribute("cartItems", cart.getItems());
    model.addAttribute("cartItemCount", cart.getItems().size());
    model.addAttribute("cartTotal", cart.getTotalAmount());

    return "user/profile";
  }

  @GetMapping("/foods/{id}")
  public String showFoodDetails(@PathVariable Long id, Model model) {
    model.addAttribute("food", foodService.getFoodById(id));
    return "user/food-detail";
  }

  @GetMapping("/checkout")
  public String showCheckoutPage(Model model, Authentication authentication) {
    log.info("Navigating to checkout page for user: {}", authentication.getName());

    UserResponseDto user = userService.getUserByUsername(authentication.getName());
    var cart = cartService.getOrCreateCart(user.getId());

    if (cart.getItems().isEmpty()) {
      return "redirect:/foods?message=Your cart is empty";
    }

    model.addAttribute("cart", cart);
    model.addAttribute("user", user);
    model.addAttribute("cartItems", cart.getItems());
    model.addAttribute("cartTotal", cart.getTotalAmount());
    model.addAttribute("cartItemCount", cart.getItems().size());

    return "user/checkout";
  }

  @PostMapping("/checkout")
  public String processCheckout(
      @RequestParam String paymentMethod,
      @RequestParam(required = false) String paymentIntentId,
      Authentication authentication,
      RedirectAttributes redirectAttributes) {
    log.info(
        "Processing checkout for user: {} with payment method: {}",
        authentication.getName(),
        paymentMethod);

    try {
      UserResponseDto user = userService.getUserByUsername(authentication.getName());
      var cart = cartService.getOrCreateCart(user.getId());

      if (cart.getItems().isEmpty()) {
        redirectAttributes.addFlashAttribute("errorMessage", "Your cart is empty");
        return "redirect:/foods";
      }

      // Create checkout request from cart items
      var orderItems =
          cart.getItems().stream()
              .<OrderItemRequestDto>map(
                  item ->
                      OrderItemRequestDto.builder()
                          .foodId(item.getFoodId())
                          .quantity(item.getQuantity())
                          .build())
              .toList();

      var checkoutRequest =
          CheckoutRequestDto.builder()
              .items(orderItems)
              .paymentMethod(paymentMethod)
              .paymentIntentId(paymentIntentId)
              .build();

      var order = orderService.createOrderWithPayment(user.getId(), checkoutRequest);

      // Clear cart only after successful order creation
      cartService.clearCart(user.getId());

      String successMessage = "Order placed successfully! Order ID: " + order.getId();
      if ("card".equals(paymentMethod)) {
        successMessage += " Payment has been processed.";
      } else {
        successMessage += " Pay cash on delivery.";
      }

      redirectAttributes.addFlashAttribute("successMessage", successMessage);
      return "redirect:/profile";

    } catch (Exception e) {
      log.error("Checkout failed for user {}: {}", authentication.getName(), e.getMessage());

      String errorMessage = "Checkout failed: " + e.getMessage();
      if (e.getMessage().contains("Payment failed")) {
        errorMessage = "Payment failed. Please check your card details and try again.";
      }

      redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
      return "redirect:/checkout";
    }
  }

  @GetMapping("/admin/orders")
  public String showAdminOrders(
      @RequestParam(value = "status", required = false) String status, Model model) {
    log.info("Admin navigating to orders page with status filter: {}", status);

    if (status != null && !status.isEmpty()) {
      try {
        OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
        model.addAttribute("orders", orderService.getOrdersByStatus(orderStatus));
      } catch (IllegalArgumentException e) {
        log.warn("Invalid status filter: {}", status);
        model.addAttribute("orders", orderService.getAllOrders());
      }
    } else {
      model.addAttribute("orders", orderService.getAllOrders());
    }

    model.addAttribute("orderStatuses", OrderStatus.values());
    model.addAttribute("currentStatus", status);
    return "admin/orders";
  }

  @GetMapping("/admin/orders/{id}")
  public String showOrderDetails(@PathVariable Long id, Model model) {
    log.info("Admin viewing order details for order ID: {}", id);
    try {
      var order = orderService.getOrderById(id);
      model.addAttribute("order", order);
      model.addAttribute("orderStatuses", OrderStatus.values());
      return "admin/order-detail";
    } catch (IllegalArgumentException e) {
      log.error("Order not found: {}", e.getMessage());
      return "redirect:/admin/orders?error=Order not found";
    }
  }

  @PostMapping("/admin/orders/{id}/status")
  public String updateOrderStatus(
      @PathVariable Long id,
      @RequestParam("status") String status,
      RedirectAttributes redirectAttributes) {
    log.info("Admin updating order {} status to: {}", id, status);

    try {
      OrderStatus newStatus = OrderStatus.valueOf(status.toUpperCase());
      orderService.updateOrderStatus(id, newStatus);
      redirectAttributes.addFlashAttribute("successMessage", "Order status updated successfully");
    } catch (IllegalArgumentException e) {
      log.error("Invalid status or order not found: {}", e.getMessage());
      redirectAttributes.addFlashAttribute("errorMessage", "Failed to update order status");
    } catch (Exception e) {
      log.error("Error updating order status: {}", e.getMessage());
      redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
    }

    return "redirect:/admin/orders/" + id;
  }
}
