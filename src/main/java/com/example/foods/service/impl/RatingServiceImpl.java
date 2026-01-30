package com.example.foods.service.impl;

import com.example.foods.entity.Food;
import com.example.foods.entity.Rating;
import com.example.foods.repository.FoodRepository;
import com.example.foods.repository.RatingRepository;
import com.example.foods.repository.UserRepository;
import com.example.foods.service.RatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RatingServiceImpl implements RatingService {

  private final RatingRepository ratingRepository;
  private final FoodRepository foodRepository;
  private final UserRepository userRepository;

  @Override
  public void submitRating(Long foodId, Long userId, Integer score, String comment) {
    if (score == null || score < 1 || score > 5) {
      throw new IllegalArgumentException("Score must be between 1 and 5");
    }

    Food food =
        foodRepository
            .findById(foodId)
            .orElseThrow(() -> new IllegalArgumentException("Food not found with ID: " + foodId));

    var user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

    var existing = ratingRepository.findByFoodIdAndUserId(foodId, userId);
    if (existing.isPresent()) {
      Rating r = existing.get();
      r.setScore(score);
      r.setComment(comment);
      ratingRepository.save(r);
      log.info("Updated rating for food {} by user {}", foodId, userId);
    } else {
      Rating rating = Rating.builder().food(food).user(user).score(score).comment(comment).build();
      ratingRepository.save(rating);
      log.info("Saved rating {} for food id {} by user {}", score, foodId, userId);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public Double getAverageRating(Long foodId) {
    Double avg = ratingRepository.findAverageByFoodId(foodId);
    return avg == null ? 0.0 : avg;
  }

  @Override
  @Transactional(readOnly = true)
  public Long getRatingCount(Long foodId) {
    Long count = ratingRepository.countByFoodId(foodId);
    return count == null ? 0L : count;
  }

  @Override
  @Transactional(readOnly = true)
  public com.example.foods.dto.response.RatingResponseDto getUserRating(Long foodId, Long userId) {
    return ratingRepository
        .findByFoodIdAndUserId(foodId, userId)
        .map(
            r ->
                com.example.foods.dto.response.RatingResponseDto.builder()
                    .id(r.getId())
                    .foodId(r.getFood() != null ? r.getFood().getId() : null)
                    .userId(r.getUser() != null ? r.getUser().getId() : null)
                    .score(r.getScore())
                    .comment(r.getComment())
                    .createdAt(r.getCreatedAt())
                    .updatedAt(r.getUpdatedAt())
                    .build())
        .orElse(null);
  }
}
