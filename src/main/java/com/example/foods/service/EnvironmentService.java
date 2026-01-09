package com.example.foods.service;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnvironmentService {

  private final Dotenv dotenv;

  public String getEnv(String key, String defaultValue) {
    String value = dotenv.get(key, defaultValue);
    if (value.equals(defaultValue)) {
      log.debug("Using default value for environment variable: {}", key);
    }
    return value;
  }

  public String getEnv(String key) {
    return dotenv.get(key);
  }

  public boolean hasEnv(String key) {
    return dotenv.get(key) != null;
  }

  public String getDatabaseUrl() {
    String host = getEnv("DB_HOST", "localhost");
    String port = getEnv("DB_PORT", "5432");
    String name = getEnv("DB_NAME", "mydatabase");
    return String.format("jdbc:postgresql://%s:%s/%s", host, port, name);
  }

  public String getDatabaseUsername() {
    return getEnv("DB_USERNAME", "myuser");
  }

  public String getDatabasePassword() {
    return getEnv("DB_PASSWORD", "secret");
  }

  public String getGoogleClientId() {
    return getEnv("GOOGLE_CLIENT_ID");
  }

  public String getGoogleClientSecret() {
    return getEnv("GOOGLE_CLIENT_SECRET");
  }
}
