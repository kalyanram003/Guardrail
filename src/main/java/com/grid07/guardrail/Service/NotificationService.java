package com.grid07.guardrail.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final long NOTIF_COOLDOWN_SECONDS = 900L;

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