package com.example.foods.repository;

import com.example.foods.entity.FoodSuggestion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodSuggestionRepository extends JpaRepository<FoodSuggestion, Long> {
  List<FoodSuggestion> findByStatus(String status);
}
