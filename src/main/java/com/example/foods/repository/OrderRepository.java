package com.example.foods.repository;

import com.example.foods.constant.OrderStatus;
import com.example.foods.entity.Order;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
  List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

  List<Order> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, String status);

  List<Order> findAllByOrderByCreatedAtDesc();

  List<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status);

  @Query(
      "SELECT DISTINCT o FROM Order o "
          + "LEFT JOIN FETCH o.user "
          + "LEFT JOIN FETCH o.items i "
          + "LEFT JOIN FETCH i.food "
          + "ORDER BY o.createdAt DESC")
  List<Order> findAllWithDetailsOrderByCreatedAtDesc();

  @Query(
      "SELECT DISTINCT o FROM Order o "
          + "LEFT JOIN FETCH o.user "
          + "LEFT JOIN FETCH o.items i "
          + "LEFT JOIN FETCH i.food "
          + "WHERE o.status = :status "
          + "ORDER BY o.createdAt DESC")
  List<Order> findByStatusWithDetailsOrderByCreatedAtDesc(OrderStatus status);

  @Query(
      "SELECT DISTINCT o FROM Order o "
          + "LEFT JOIN FETCH o.user "
          + "LEFT JOIN FETCH o.items i "
          + "LEFT JOIN FETCH i.food "
          + "WHERE o.user.id = :userId "
          + "ORDER BY o.createdAt DESC")
  List<Order> findByUserIdWithDetailsOrderByCreatedAtDesc(Long userId);

  @Query(
      "SELECT o FROM Order o "
          + "LEFT JOIN FETCH o.user "
          + "LEFT JOIN FETCH o.items i "
          + "LEFT JOIN FETCH i.food "
          + "WHERE o.id = :orderId")
  Order findByIdWithDetails(Long orderId);
}
