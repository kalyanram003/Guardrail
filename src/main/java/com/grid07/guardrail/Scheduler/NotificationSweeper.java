package com.grid07.guardrail.Scheduler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationSweeper {
    private final RedisTemplate<String, Object> redisTemplate;

    @Scheduled(fixedRate = 300_000)
    public void sweepPendingNotifications() {
        log.info("Notification Sweeper running at {}", LocalDateTime.now());

        Set<String> keys = redisTemplate.keys("user:*:pending_notifs");

        if (keys == null || keys.isEmpty()) {
            log.info("No pending notifications found.");
            return;
        }

        for (String key : keys) {
            List<Object> messages = redisTemplate.opsForList().range(key, 0, -1);

            if (messages == null || messages.isEmpty()) continue;

            int count = messages.size();
            String firstMessage = messages.get(0).toString();

            String botName = firstMessage.contains(" replied")
                ? firstMessage.split(" replied")[0]
                : "A bot";
            if (count == 1) {
                log.info("Summarized Push Notification: {}", firstMessage);
            } else {
                log.info("Summarized Push Notification: {} and [{}] others interacted with your posts.",
                    botName, count - 1);
            }
            redisTemplate.delete(key);
        }
    }
}
