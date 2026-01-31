package com.example.foods.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "food_suggestions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodSuggestion {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(nullable = false)
  private String name;

  @Column(length = 1000)
  private String description;

  @Column(nullable = false)
  private Double price;

  @Column(nullable = false)
  private String category;

  @Column(name = "image_url")
  private String imageUrl;

  @Column(length = 50)
  @Builder.Default
  private String status = "PENDING";

  @Column(name = "admin_note", length = 1000)
  private String adminNote;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }
}
