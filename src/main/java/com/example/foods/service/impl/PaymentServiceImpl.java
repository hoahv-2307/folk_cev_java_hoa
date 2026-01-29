package com.example.foods.service.impl;

import com.example.foods.dto.request.PaymentIntentRequestDto;
import com.example.foods.dto.response.PaymentIntentResponseDto;
import com.example.foods.service.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCancelParams;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

  @Value("${stripe.publishable.key}")
  private String publishableKey;

  @Override
  public PaymentIntentResponseDto createPaymentIntent(PaymentIntentRequestDto request) {
    try {
      log.info("Creating payment intent for amount: {} cents", request.getAmount());

      PaymentIntentCreateParams.Builder paramsBuilder =
          PaymentIntentCreateParams.builder()
              .setAmount(request.getAmount())
              .setCurrency(request.getCurrency())
              .setAutomaticPaymentMethods(
                  PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                      .setEnabled(true)
                      .build());

      if (request.getDescription() != null) {
        paramsBuilder.setDescription(request.getDescription());
      }

      if (request.getReceiptEmail() != null) {
        paramsBuilder.setReceiptEmail(request.getReceiptEmail());
      }

      PaymentIntent intent = PaymentIntent.create(paramsBuilder.build());

      log.info("Payment intent created successfully with ID: {}", intent.getId());

      return PaymentIntentResponseDto.builder()
          .clientSecret(intent.getClientSecret())
          .paymentIntentId(intent.getId())
          .amount(intent.getAmount())
          .currency(intent.getCurrency())
          .status(intent.getStatus())
          .build();

    } catch (StripeException e) {
      log.error("Error creating payment intent: {}", e.getMessage(), e);
      throw new RuntimeException("Failed to create payment intent: " + e.getMessage(), e);
    }
  }

  @Override
  public boolean confirmPayment(String paymentIntentId) {
    try {
      log.info("Confirming payment intent: {}", paymentIntentId);

      PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);

      if ("succeeded".equals(paymentIntent.getStatus())) {
        log.info("Payment confirmed successfully for intent: {}", paymentIntentId);
        return true;
      } else {
        log.warn("Payment not confirmed. Status: {}", paymentIntent.getStatus());
        return false;
      }

    } catch (StripeException e) {
      log.error("Error confirming payment intent {}: {}", paymentIntentId, e.getMessage(), e);
      return false;
    }
  }

  @Override
  public void cancelPayment(String paymentIntentId) {
    try {
      log.info("Canceling payment intent: {}", paymentIntentId);

      PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);

      if ("requires_payment_method".equals(paymentIntent.getStatus())
          || "requires_confirmation".equals(paymentIntent.getStatus())
          || "requires_action".equals(paymentIntent.getStatus())) {

        PaymentIntentCancelParams params = PaymentIntentCancelParams.builder().build();
        paymentIntent.cancel(params);

        log.info("Payment intent canceled successfully: {}", paymentIntentId);
      } else {
        log.warn(
            "Cannot cancel payment intent {} with status: {}",
            paymentIntentId,
            paymentIntent.getStatus());
      }

    } catch (StripeException e) {
      log.error("Error canceling payment intent {}: {}", paymentIntentId, e.getMessage(), e);
      throw new RuntimeException("Failed to cancel payment intent: " + e.getMessage(), e);
    }
  }

  @Override
  public String getPublishableKey() {
    return publishableKey;
  }
}
