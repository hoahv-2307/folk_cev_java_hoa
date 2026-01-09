package com.example.foods.repository;

import com.example.foods.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {
    
    /**
     * Find foods by category
     */
    List<Food> findByCategory(String category);
    
    /**
     * Find foods by name containing (case insensitive)
     */
    List<Food> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find foods by price range
     */
    List<Food> findByPriceBetween(Double minPrice, Double maxPrice);
    
    /**
     * Custom query to find foods by category and price range
     */
    @Query("SELECT f FROM Food f WHERE f.category = :category AND f.price BETWEEN :minPrice AND :maxPrice")
    List<Food> findByCategoryAndPriceRange(
        @Param("category") String category, 
        @Param("minPrice") Double minPrice, 
        @Param("maxPrice") Double maxPrice
    );
    
    /**
     * Check if food exists by name (case insensitive)
     */
    boolean existsByNameIgnoreCase(String name);
}
