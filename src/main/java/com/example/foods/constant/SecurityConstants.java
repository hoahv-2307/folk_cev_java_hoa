package com.example.foods.constant;

import java.util.UUID;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class SecurityConstants {
  public static final String DEFAULT_OAUTH2_PASSWORD = System.getProperty("oauth2.default.password", UUID.randomUUID().toString());;
}
