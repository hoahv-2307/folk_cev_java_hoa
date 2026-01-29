package com.example.foods.controller;

import com.example.foods.dto.request.PaymentIntentRequestDto;
import com.example.foods.dto.response.PaymentIntentResponseDto;
import com.example.foods.service.PaymentService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

  private final PaymentService paymentService;

  @GetMapping("/config")
  public ResponseEntity<Map<String, String>> getStripeConfig() {
    log.info("REST request to get Stripe configuration");

    return ResponseEntity.ok(Map.of("publishableKey", paymentService.getPublishableKey()));
  }

  @PostMapping("/create-payment-intent")
  public ResponseEntity<PaymentIntentResponseDto> createPaymentIntent(
      @Valid @RequestBody PaymentIntentRequestDto request, Principal principal) {
    log.info(
        "REST request to create payment intent - User: {}, Amount: {} cents",
        principal.getName(),
        request.getAmount());

    PaymentIntentResponseDto response = paymentService.createPaymentIntent(request);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/confirm/{paymentIntentId}")
  public ResponseEntity<Map<String, Object>> confirmPayment(
      @PathVariable String paymentIntentId, Principal principal) {
    log.info(
        "REST request to confirm payment - User: {}, PaymentIntent: {}",
        principal.getName(),
        paymentIntentId);

    boolean confirmed = paymentService.confirmPayment(paymentIntentId);

    return ResponseEntity.ok(
        Map.of(
            "confirmed", confirmed,
            "paymentIntentId", paymentIntentId));
  }

  @PostMapping("/cancel/{paymentIntentId}")
  public ResponseEntity<Map<String, String>> cancelPayment(
      @PathVariable String paymentIntentId, Principal principal) {
    log.info(
        "REST request to cancel payment - User: {}, PaymentIntent: {}",
        principal.getName(),
        paymentIntentId);

    paymentService.cancelPayment(paymentIntentId);

    return ResponseEntity.ok(
        Map.of("message", "Payment canceled successfully", "paymentIntentId", paymentIntentId));
  }
}
