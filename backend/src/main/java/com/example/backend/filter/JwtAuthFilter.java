package com.example.backend.filter;

import com.example.backend.exception.NotFoundException;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.JwtProvider;
import com.example.backend.service.RedisProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final List<String> PERMITTED_PATHS = List.of(
            "/auth/login", "/auth/register", "/oauth2", "/auth/new-token-pair",
            "/swagger-ui", "/v3/api-docs"
    );
    private final JwtProvider jwtProvider;
    private final RedisProvider redisProvider;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        final String path = request.getRequestURI();
        boolean isPermitted = PERMITTED_PATHS.stream().anyMatch(path::startsWith);

        if (isPermitted) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String jwt = getTokenFromRequest(request);

            // должен иметься токен в заголовках,
            // не должно быть в redis, тк туда добавляются невалидные access, при logout
            // и токен должен быть валидным
            if (jwt != null && !redisProvider.hasKey(jwt) && jwtProvider.validateAccessToken(jwt)) {

                String userEmail = jwtProvider.getAccessClaims(jwt).getSubject();
                UserDetails userDetails = userRepository.findByEmail(userEmail)
                        .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

                // Создаем объект аутентификации
                // Мы передаем userDetails и его права (authorities)
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // Пароль не нужен, т.к. используем JWT
                        userDetails.getAuthorities()
                );

                // Добавляем детали запроса в объект аутентификации(ip, id сессии)
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        final String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(authHeader) && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}