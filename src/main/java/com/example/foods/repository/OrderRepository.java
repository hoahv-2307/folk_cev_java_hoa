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

  @Query(
      "SELECT o FROM Order o WHERE o.user.id = :userId AND o.status = :status ORDER BY o.createdAt DESC")
  List<Order> findAllByOrderByCreatedAtDesc();

  @Query("SELECT o FROM Order o WHERE o.status = :status ORDER BY o.createdAt DESC")
  List<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status);
}
