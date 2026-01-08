package com.example.foods.config;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class EnvironmentConfig {

    @Bean
    public Dotenv dotenv() {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .directory(".")
                    .filename(".env")
                    .ignoreIfMissing()
                    .load();
            log.info("Successfully loaded .env file");
            dotenv.entries().forEach(entry -> {
                String key = entry.getKey();
                String value = entry.getValue();
                if (System.getProperty(key) == null && System.getenv(key) == null) {
                    System.setProperty(key, value);
                }
            });

            return dotenv;
        } catch (Exception e) {
            log.warn("Could not load .env file: {}", e.getMessage());
            return Dotenv.configure().ignoreIfMissing().load();
        }
    }
}
