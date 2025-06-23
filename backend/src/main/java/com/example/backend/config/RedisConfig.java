package com.example.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // --- Настройка сериализаторов ---

        // 1. Сериализатор для ключей (обычно это строки)
        template.setKeySerializer(new StringRedisSerializer());

        // 2. Сериализатор для значений (будем использовать JSON)
        // GenericJackson2JsonRedisSerializer сохраняет информацию о типе,
        // что позволяет десериализовать JSON обратно в правильный Java-объект.
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        // 3. Сериализаторы для ключей и значений в Hash-операциях
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        // Применяем настройки
        template.afterPropertiesSet();

        return template;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        // Spring Boot может создать этот бин и сам, но явное определение дает больше контроля.
        return new StringRedisTemplate(connectionFactory);
    }

}