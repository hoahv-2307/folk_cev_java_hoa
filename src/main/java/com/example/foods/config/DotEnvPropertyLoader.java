package com.example.foods.config;

import io.github.cdimascio.dotenv.Dotenv;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

@Slf4j
public class DotEnvPropertyLoader
    implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

  @Override
  public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
    try {
      Dotenv dotenv = Dotenv.configure().directory(".").filename(".env").load();

      ConfigurableEnvironment environment = event.getEnvironment();
      Map<String, Object> envMap = new HashMap<>();

      dotenv
          .entries()
          .forEach(
              entry -> {
                String key = entry.getKey();
                String value = entry.getValue();

                if (!environment.containsProperty(key)) {
                  envMap.put(key, value);
                }
              });

      if (!envMap.isEmpty()) {
        MapPropertySource propertySource = new MapPropertySource("dotenvProperties", envMap);
        environment.getPropertySources().addLast(propertySource);
        log.info("Successfully loaded {} properties from .env file", envMap.size());
      }

    } catch (Exception e) {
      log.warn("Could not load .env file: {}", e.getMessage());
    }
  }
}
