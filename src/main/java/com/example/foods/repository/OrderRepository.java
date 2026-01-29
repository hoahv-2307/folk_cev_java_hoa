package com.example.foods.repository;

import com.example.foods.constant.OrderStatus;
import com.example.foods.entity.Order;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
  List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

  List<Order> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, String status);

  List<Order> findAllByOrderByCreatedAtDesc();

  List<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status);
}
