package com.grid07.guardrail.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ViralityService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final int BOT_REPLY_POINTS     = 1;
    private static final int HUMAN_LIKE_POINTS    = 20;
    private static final int HUMAN_COMMENT_POINTS = 50;

    public void incrementBotReply(Long postId) {
        redisTemplate.opsForValue()
            .increment("post:" + postId + ":virality_score", BOT_REPLY_POINTS);
    }

    public void incrementHumanLike(Long postId) {
        redisTemplate.opsForValue()
            .increment("post:" + postId + ":virality_score", HUMAN_LIKE_POINTS);
    }

    public void incrementHumanComment(Long postId) {
        redisTemplate.opsForValue()
            .increment("post:" + postId + ":virality_score", HUMAN_COMMENT_POINTS);
    }

    public Long getViralityScore(Long postId) {
        Object val = redisTemplate.opsForValue().get("post:" + postId + ":virality_score");
        return val != null ? Long.parseLong(val.toString()) : 0L;
    }
}