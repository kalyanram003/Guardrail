package com.grid07.guardrail.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class GuardrailService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final int HORIZONTAL_CAP = 100;
    private static final int VERTICAL_CAP   = 20;
    private static final long COOLDOWN_TTL  = 600L;

    public boolean tryIncrementBotCount(Long postId) {
        String key = "post:" + postId + ":bot_count";

        // Lua Script
        String luaScript =
            "local count = redis.call('INCR', KEYS[1]) " +
            "if count > tonumber(ARGV[1]) then " +
            "  redis.call('DECR', KEYS[1]) " +
            "  return 0 " +
            "else " +
            "  return count " +
            "end";

        Long result = redisTemplate.execute(
            new DefaultRedisScript<>(luaScript, Long.class),
            Collections.singletonList(key),
            String.valueOf(HORIZONTAL_CAP)
        );

        return result != null && result > 0;
    }

    public void decrementBotCount(Long postId) {
        redisTemplate.opsForValue().decrement("post:" + postId + ":bot_count");
    }

    public boolean isDepthAllowed(int depthLevel) {
        return depthLevel <= VERTICAL_CAP;
    }

    public boolean trySetCooldown(Long botId, Long humanId) {
        String key = "cooldown:bot_" + botId + ":human_" + humanId;
        Boolean wasSet = redisTemplate.opsForValue()
            .setIfAbsent(key, "1", Duration.ofSeconds(COOLDOWN_TTL));
        return Boolean.TRUE.equals(wasSet);
    }
}