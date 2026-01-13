package com.example.foods.config;

import com.example.foods.repository.FoodRepository;
import com.example.foods.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class DataLoaderTest {

  @Autowired
  private FoodRepository foodRepository;

  @Autowired
  private UserRepository userRepository;

  @Test
  void shouldLoadSampleDataOnStartup() {
    assertThat(foodRepository.count()).isGreaterThan(0);
    assertThat(userRepository.count()).isGreaterThan(0);

    assertThat(foodRepository.count()).isEqualTo(5);
    assertThat(userRepository.count()).isEqualTo(2);

    assertThat(userRepository.findByUsername("admin")).isNotNull();
    assertThat(userRepository.findByUsername("user")).isNotNull();
  }
}
