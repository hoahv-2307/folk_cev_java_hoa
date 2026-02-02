package com.example.foods.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "foods")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Food {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(length = 1000)
  private String description;

  @Column(nullable = false)
  private String category;

  @Column(nullable = false)
  private Double price;

  @Column(nullable = false)
  @Builder.Default
  private Integer quantity = 0;

  @Column(length = 50)
  @Builder.Default
  private String status = "ACTIVE";

  @Version private Long version;

  @OneToMany(mappedBy = "food", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<FoodImage> foodImages = new ArrayList<>();

  @Column(name = "view_count", nullable = false)
  @Builder.Default
  private Long viewCount = 0L;

  @Column(name = "order_count", nullable = false)
  @Builder.Default
  private Long orderCount = 0L;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  public void addFoodImage(FoodImage foodImage) {
    this.foodImages.add(foodImage);
    foodImage.setFood(this);
  }

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }
}
