package com.example.foods.repository;

import com.example.foods.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

  @Query("SELECT AVG(r.score) FROM Rating r WHERE r.food.id = :foodId")
  Double findAverageByFoodId(@Param("foodId") Long foodId);

  @Query("SELECT COUNT(r) FROM Rating r WHERE r.food.id = :foodId")
  Long countByFoodId(@Param("foodId") Long foodId);

  @Query("SELECT r FROM Rating r WHERE r.food.id = :foodId AND r.user.id = :userId")
  java.util.Optional<Rating> findByFoodIdAndUserId(
      @Param("foodId") Long foodId, @Param("userId") Long userId);
}
