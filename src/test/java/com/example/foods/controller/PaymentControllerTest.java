package com.example.foods.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.foods.dto.request.PaymentIntentRequestDto;
import com.example.foods.dto.response.PaymentIntentResponseDto;
import com.example.foods.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

class PaymentControllerTest {

  @Autowired private MockMvc mockMvc;

  private PaymentService paymentService;

  @Autowired private ObjectMapper objectMapper;

  @Test
  @WithMockUser
  void testGetStripeConfig() throws Exception {
    when(paymentService.getPublishableKey()).thenReturn("pk_test_example");

    mockMvc
        .perform(get("/api/payments/config"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.publishableKey").value("pk_test_example"));
  }

  @Test
  @WithMockUser
  void testCreatePaymentIntent() throws Exception {
    PaymentIntentRequestDto request =
        PaymentIntentRequestDto.builder()
            .amount(2000L)
            .currency("usd")
            .description("Test payment")
            .build();

    PaymentIntentResponseDto response =
        PaymentIntentResponseDto.builder()
            .clientSecret("pi_test_client_secret")
            .paymentIntentId("pi_test_123")
            .amount(2000L)
            .currency("usd")
            .status("requires_payment_method")
            .build();

    when(paymentService.createPaymentIntent(any(PaymentIntentRequestDto.class)))
        .thenReturn(response);

    mockMvc
        .perform(
            post("/api/payments/create-payment-intent")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.clientSecret").value("pi_test_client_secret"))
        .andExpect(jsonPath("$.paymentIntentId").value("pi_test_123"))
        .andExpect(jsonPath("$.amount").value(2000))
        .andExpect(jsonPath("$.currency").value("usd"));
  }

  @Test
  @WithMockUser
  void testConfirmPayment() throws Exception {
    when(paymentService.confirmPayment("pi_test_123")).thenReturn(true);

    mockMvc
        .perform(post("/api/payments/confirm/pi_test_123"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.confirmed").value(true))
        .andExpect(jsonPath("$.paymentIntentId").value("pi_test_123"));
  }
}
