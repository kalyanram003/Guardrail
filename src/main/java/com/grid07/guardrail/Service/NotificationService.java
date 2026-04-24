package com.grid07.guardrail.Service;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NotificationService {
    private final RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();

    private static final long NOTIF_COOLDOWN_SECONDS = 900L;

    // Handle bot interaction with a user
    public void handleBotInteraction(Long humanUserId, Long botId) {
        String cooldownKey  = "notif_cooldown:user_" + humanUserId;
        String pendingKey   = "user:" + humanUserId + ":pending_notifs";
        String notifMessage = "Bot " + botId + " replied to your post";

        Boolean isOnCooldown = redisTemplate.hasKey(cooldownKey);

        if (Boolean.TRUE.equals(isOnCooldown)) {
            redisTemplate.opsForList().rightPush(pendingKey, notifMessage);
            log.info("Queued pending notification for user {}: {}", humanUserId, notifMessage);
        } else {
            log.info("Push Notification Sent to User {}: {}", humanUserId, notifMessage);
            redisTemplate.opsForValue()
                .set(cooldownKey, "1", Duration.ofSeconds(NOTIF_COOLDOWN_SECONDS));
        }
    }
}
