package com.example.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://localhost:8081", // Порт, на котором будет работать ваш фронтенд
                        "http://127.0.0.1:5500", "http://localhost:5500"  // Пример для Live Server в VS Code
                        // "http://your-production-domain.com" // В будущем добавите домен продакшена
                )
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true) // Разрешаем передачу cookie и заголовка Authorization
                .maxAge(3600);
    }
}