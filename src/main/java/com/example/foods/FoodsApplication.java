package com.example.foods;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class FoodsApplication {

  public static void main(String[] args) {
    SpringApplication.run(FoodsApplication.class, args);
  }
}
