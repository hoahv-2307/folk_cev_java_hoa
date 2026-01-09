package com.example.foods.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // Handle favicon.ico requests
    registry
        .addResourceHandler("/favicon.ico")
        .addResourceLocations("classpath:/static/")
        .setCachePeriod(31556926); // 1 year cache

    // Handle other static resources
    registry
        .addResourceHandler("/static/**")
        .addResourceLocations("classpath:/static/")
        .setCachePeriod(31556926);
  }
}
