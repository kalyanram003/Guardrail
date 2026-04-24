package com.grid07.guardrail.Service;

import java.util.Collections;

import org.springframework.data.redis.core.RedisTemplate;
import java.time.Duration;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GuardrailService {
    private final RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();

    private static final int HORIZONTAL_CAP  = 100;
    private static final int VERTICAL_CAP    = 20;
    private static final long COOLDOWN_TTL   = 600L; 

    // Lua script for bot comment count
    public boolean tryIncrementBotCount(Long postId) {
        String key = "post:" + postId + ":bot_count";
        String luaScript ="local count = redis.call('INCR', KEYS[1]) " + "if count > tonumber(ARGV[1]) then " + "  redis.call('DECR', KEYS[1]) " + "  return 0 " + "else " + "  return count " + "end";

        Long result = redisTemplate.execute(new DefaultRedisScript<>(luaScript, Long.class), Collections.singletonList(key), String.valueOf(HORIZONTAL_CAP));
        return result != null && result > 0;
    }

    public void decrementBotCount(Long postId) {
        String key = "post:" + postId + ":bot_count";
        redisTemplate.opsForValue().decrement(key);
    }

    public boolean isDepthAllowed(int depthLevel) {
        return depthLevel <= VERTICAL_CAP;
    }
    
    public boolean trySetCooldown(Long botId, Long humanId) {
        String key = "cooldown:bot " + botId + ":human " + humanId;
        Boolean wasSet = redisTemplate.opsForValue().setIfAbsent(key, "1", Duration.ofSeconds(COOLDOWN_TTL));
        return Boolean.TRUE.equals(wasSet);
    }
}
