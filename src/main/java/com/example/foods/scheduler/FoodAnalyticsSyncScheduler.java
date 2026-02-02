package com.example.foods.scheduler;

import com.example.foods.service.FoodAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FoodAnalyticsSyncScheduler {

  private final FoodAnalyticsService foodAnalyticsService;

  @Scheduled(cron = "0 * * * * *")
  @SchedulerLock(name = "syncFoodAnalytics", lockAtLeastFor = "PT5M", lockAtMostFor = "PT30M")
  public void syncFoodAnalyticsToDatabase() {
    log.info("Starting scheduled sync of food analytics from Redis to database");
    try {
      foodAnalyticsService.syncRedisToDatabase();
      log.info("Completed scheduled sync of food analytics");
    } catch (Exception e) {
      log.error("Error during scheduled sync of food analytics", e);
    }
  }
}
