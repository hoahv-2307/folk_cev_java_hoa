package com.example.foods.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class StripeConfig {

  @Value("${stripe.secret.key}")
  private String secretKey;

  @PostConstruct
  public void initSecretKey() {
    Stripe.apiKey = secretKey;
    log.info("Stripe configured successfully");
  }
}
