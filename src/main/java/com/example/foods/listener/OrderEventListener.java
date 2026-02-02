package com.example.foods.listener;

import com.example.foods.event.OrderCreatedEvent;
import com.example.foods.service.FoodAnalyticsService;
import com.example.foods.service.MailService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
@AllArgsConstructor
public class OrderEventListener {
  private final MailService mailService;
  private final FoodAnalyticsService foodAnalyticsService;

  @Value("${spring.application.base-url}")
  private String appBaseUrl;

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onOrderCreated(OrderCreatedEvent event) {
    log.info("Handling OrderCreatedEvent for Order ID: {}", event.getOrder().getId());
    String emailBody =
            """
        Order ID: %d has been placed by user: %s. Total amount: $%.2f
        Check at URL: %s/admin/orders/%d
        """
            .formatted(
                event.getOrder().getId(),
                event.getOrder().getUser().getUsername(),
                event.getOrder().getTotalAmount(),
                appBaseUrl,
                event.getOrder().getId());
    mailService.sendSimpleMail("A new order has been placed", emailBody);
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onOrderCreatedIncrementOrderCount(OrderCreatedEvent event) {
    log.info("Incrementing order counts for Order ID: {}", event.getOrder().getId());
    event
        .getOrder()
        .getItems()
        .forEach(
            item ->
                foodAnalyticsService.incrementOrderCount(
                    item.getFood().getId(), item.getQuantity()));
  }
}
