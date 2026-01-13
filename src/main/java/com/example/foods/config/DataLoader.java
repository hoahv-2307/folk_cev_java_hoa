package com.example.foods.config;

import com.example.foods.entity.Food;
import com.example.foods.entity.User;
import com.example.foods.repository.FoodRepository;
import com.example.foods.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataLoader {

  @Bean
  @Profile({ "dev", "local", "test" })
  CommandLineRunner initDatabase(
      FoodRepository foodRepository,
      UserRepository userRepository,
      PasswordEncoder passwordEncoder) {
    return args -> {
      log.info("Loading sample data...");

      if (foodRepository.count() > 0) {
        log.info("Database already contains data, skipping initialization");
        return;
      }

      User adminUser = User.builder()
          .username("admin")
          .email("admin@foods.com")
          .password(passwordEncoder.encode("admin123"))
          .role("ADMIN")
          .build();

      User regularUser = User.builder()
          .username("user")
          .email("user@foods.com")
          .password(passwordEncoder.encode("user123"))
          .role("USER")
          .build();

      userRepository.save(adminUser);
      userRepository.save(regularUser);

      // Create sample food items
      Food pizza = Food.builder()
          .name("Margherita Pizza")
          .description("Classic pizza with tomato sauce, mozzarella, and basil")
          .category("Italian")
          .price(12.99)
          .build();

      Food burger = Food.builder()
          .name("Beef Burger")
          .description("Juicy beef patty with lettuce, tomato, and cheese")
          .category("American")
          .price(9.99)
          .build();

      Food sushi = Food.builder()
          .name("Salmon Sushi")
          .description("Fresh salmon sushi with rice and wasabi")
          .category("Japanese")
          .price(15.50)
          .build();

      Food pasta = Food.builder()
          .name("Spaghetti Carbonara")
          .description("Creamy pasta with bacon, eggs, and parmesan cheese")
          .category("Italian")
          .price(11.75)
          .build();

      Food tacos = Food.builder()
          .name("Chicken Tacos")
          .description("Soft tacos with grilled chicken, salsa, and avocado")
          .category("Mexican")
          .price(8.25)
          .build();

      // Save sample data
      foodRepository.save(pizza);
      foodRepository.save(burger);
      foodRepository.save(sushi);
      foodRepository.save(pasta);
      foodRepository.save(tacos);

      log.info("Sample data loaded successfully!");
      log.info("Total food items: {}", foodRepository.count());
      log.info("Total users: {}", userRepository.count());
      log.info("Demo users created with usernames: admin, user");
    };
  }
}
