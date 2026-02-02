package com.example.foods.service.impl;

import com.example.foods.dto.response.FoodAnalyticsResponseDto;
import com.example.foods.repository.FoodRepository;
import com.example.foods.service.FoodAnalyticsService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FoodAnalyticsServiceImpl implements FoodAnalyticsService {

  private static final String VIEW_COUNT_KEY_PREFIX = "food:view:";
  private static final String ORDER_COUNT_KEY_PREFIX = "food:order:";

  private final StringRedisTemplate stringRedisTemplate;
  private final FoodRepository foodRepository;
  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Override
  public void incrementViewCount(Long foodId) {
    String key = VIEW_COUNT_KEY_PREFIX + foodId;
    stringRedisTemplate.opsForValue().increment(key);
    log.debug("Incremented view count for food ID: {}", foodId);
  }

  @Override
  public void incrementOrderCount(Long foodId, int quantity) {
    String key = ORDER_COUNT_KEY_PREFIX + foodId;
    stringRedisTemplate.opsForValue().increment(key, quantity);
    log.debug("Incremented order count for food ID: {} by {}", foodId, quantity);
  }

  @Override
  @Transactional(readOnly = true)
  public List<FoodAnalyticsResponseDto> getAllFoodAnalytics() {
    log.info("Retrieving all food analytics from database");
    return foodRepository.findAllFoodAnalytics();
  }

  @Override
  @Transactional
  public void syncRedisToDatabase() {
    log.info("Starting sync of Redis analytics data to database");

    Map<Long, Long> viewCounts = getAndClearCountsByPrefix(VIEW_COUNT_KEY_PREFIX);
    Map<Long, Long> orderCounts = getAndClearCountsByPrefix(ORDER_COUNT_KEY_PREFIX);

    if (viewCounts.isEmpty() && orderCounts.isEmpty()) {
      log.info("No analytics data to sync from Redis");
      return;
    }

    batchUpdateAnalytics(viewCounts, orderCounts);
    log.info(
        "Successfully synced analytics data: {} view counts, {} order counts",
        viewCounts.size(),
        orderCounts.size());
  }

  private Map<Long, Long> getAndClearCountsByPrefix(String prefix) {
    Map<Long, Long> counts = new HashMap<>();
    Set<String> keys = stringRedisTemplate.keys(prefix + "*");

    if (keys == null || keys.isEmpty()) {
      return counts;
    }

    for (String key : keys) {
      String value = stringRedisTemplate.opsForValue().getAndDelete(key);
      if (value != null) {
        try {
          Long foodId = Long.parseLong(key.replace(prefix, ""));
          Long count = Long.parseLong(value);
          counts.put(foodId, count);
        } catch (NumberFormatException e) {
          log.warn("Invalid key or value format: {} = {}", key, value);
        }
      }
    }

    return counts;
  }

  private void batchUpdateAnalytics(Map<Long, Long> viewCounts, Map<Long, Long> orderCounts) {
    Map<Long, AnalyticsUpdate> updates = new HashMap<>();

    viewCounts.forEach(
        (foodId, count) ->
            updates.computeIfAbsent(foodId, k -> new AnalyticsUpdate(foodId, 0L, 0L)).viewCount =
                count);

    orderCounts.forEach(
        (foodId, count) ->
            updates.computeIfAbsent(foodId, k -> new AnalyticsUpdate(foodId, 0L, 0L)).orderCount =
                count);

    if (updates.isEmpty()) {
      return;
    }

    String sql =
        "UPDATE foods SET view_count = view_count + :viewCount, order_count = order_count + :orderCount WHERE id = :foodId";

    SqlParameterSource[] batchParams =
        SqlParameterSourceUtils.createBatch(updates.values().toArray());

    namedParameterJdbcTemplate.batchUpdate(sql, batchParams);
    log.info("Batch updated {} food analytics records", updates.size());
  }

  private static class AnalyticsUpdate {
    Long foodId;
    Long viewCount;
    Long orderCount;

    AnalyticsUpdate(Long foodId, Long viewCount, Long orderCount) {
      this.foodId = foodId;
      this.viewCount = viewCount;
      this.orderCount = orderCount;
    }

    public Long getFoodId() {
      return foodId;
    }

    public Long getViewCount() {
      return viewCount;
    }

    public Long getOrderCount() {
      return orderCount;
    }
  }
}
