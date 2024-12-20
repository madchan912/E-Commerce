package com.sparta.userservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class TokenService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public TokenService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 사용자별 토큰 저장
    public void saveUserToken(String userId, String token, long expiration) {
        String key = "user:" + userId + ":tokens";
        redisTemplate.opsForList().rightPush(key, token); // 사용자 토큰 리스트에 추가
        redisTemplate.expire(key, expiration, TimeUnit.MILLISECONDS); // 만료 시간 설정
    }

    // 사용자별 저장된 토큰 조회
    public List<Object> getUserTokens(String userId) {
        String key = "user:" + userId;
        return redisTemplate.opsForList().range(key, 0, -1); // 모든 토큰 조회
    }

    // 사용자의 모든 토큰을 삭제
    public void deleteUserTokens(String userId) {
        String key = "user:" + userId + ":tokens";
        redisTemplate.delete(key); // Redis에서 키 삭제
    }
}
