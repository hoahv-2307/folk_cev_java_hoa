package com.example.foods.service;

import com.example.foods.dto.request.PaymentIntentRequestDto;
import com.example.foods.dto.response.PaymentIntentResponseDto;

public interface PaymentService {
  PaymentIntentResponseDto createPaymentIntent(PaymentIntentRequestDto request);

  boolean confirmPayment(String paymentIntentId);

  void cancelPayment(String paymentIntentId);

  String getPublishableKey();
}
