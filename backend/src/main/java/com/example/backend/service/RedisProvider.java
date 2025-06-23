package com.example.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisProvider {

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate<String, Object> redisTemplate;


    public String findUserIdByToken(String token) {
        // почему-то возвращает кучу пустых символов и значение, так что обрезаем
        String userId = stringRedisTemplate.opsForValue().get(token);
        return userId == null ? null : userId.trim();
    }

    public Set<String> findTokensByUserId(String userId) {
        return redisTemplate.opsForSet().members(userId)
                .stream()
                .map(Object::toString)
                .collect(Collectors.toSet());
    }

    public Boolean hasKey(String token) {
        return stringRedisTemplate.hasKey(token);
    }

    public void addToken(String userId, String token, Long expiration) {
        // 1. Сохраняем основную связь: token -> userId
        stringRedisTemplate.opsForValue().set(token, userId, expiration);

        // 2. Добавляем токен в множество токенов пользователя: user_tokens:{userId} -> {token1, token2, ...}
        redisTemplate.opsForSet().add(userId, token);
        // Устанавливаем TTL для всего множества, будет перезаписывать для всех,
        // но в deleteToken мы удаляем из списка токен и если всё будет нормально, то ttl для множества вообще не нужно
        redisTemplate.expire(userId, expiration, TimeUnit.MILLISECONDS);
    }

    public void deleteToken(String token) {
        final String userId = findUserIdByToken(token);
        if (userId != null) {
            redisTemplate.opsForSet().remove(userId, token);
        }
        stringRedisTemplate.delete(token);
    }

    public void deleteAllTokensByUserId(String userId) {
        // Получаем все токены пользователя
        Set<String> tokens = findTokensByUserId(userId);
        if (tokens != null && !tokens.isEmpty()) {
            // Удаляем все ключи token -> userId
            stringRedisTemplate.delete(tokens);
        }
        // Удаляем само множество токенов пользователя
        redisTemplate.delete(userId);
    }
}
