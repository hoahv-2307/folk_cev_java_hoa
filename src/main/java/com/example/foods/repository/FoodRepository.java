package com.example.foods.repository;

import com.example.foods.entity.Food;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {

  @Query("SELECT DISTINCT f FROM Food f LEFT JOIN FETCH f.foodImages")
  List<Food> findAll();

  @Query("SELECT DISTINCT f FROM Food f LEFT JOIN FETCH f.foodImages WHERE f.id = :id")
  Optional<Food> findById(@Param("id") Long id);

  @Query("SELECT DISTINCT f FROM Food f LEFT JOIN FETCH f.foodImages WHERE f.category = :category")
  List<Food> findByCategory(@Param("category") String category);

  @Query(
      "SELECT DISTINCT f FROM Food f LEFT JOIN FETCH f.foodImages WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', :name, '%'))")
  List<Food> findByNameContainingIgnoreCase(@Param("name") String name);

  @Query(
      "SELECT DISTINCT f FROM Food f LEFT JOIN FETCH f.foodImages WHERE f.price BETWEEN :minPrice AND :maxPrice")
  List<Food> findByPriceBetween(
      @Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);

  @Query(
      "SELECT DISTINCT f FROM Food f LEFT JOIN FETCH f.foodImages WHERE f.category = :category AND f.price BETWEEN :minPrice AND :maxPrice")
  List<Food> findByCategoryAndPriceRange(
      @Param("category") String category,
      @Param("minPrice") Double minPrice,
      @Param("maxPrice") Double maxPrice);

  boolean existsByNameIgnoreCase(String name);
}
