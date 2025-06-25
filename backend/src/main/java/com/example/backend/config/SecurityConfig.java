package com.example.backend.config;

import com.example.backend.filter.JwtAuthFilter;
import com.example.backend.filter.LoggingFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final LoggingFilter loggingFilter;
    private final JwtAuthFilter jwtAuthFilter;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final ObjectMapper objectMapper;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //http://localhost:12345/login/oauth2/code/google
    //http://localhost:12345/oauth2/authorization/google


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .exceptionHandling(exceptions -> exceptions

                        // обработчик на .authenticated эндпоинты
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding("UTF-8");
                            String errorJson = String.format(
                                    "{\"status\": %d, \"message\": \"%s\"}",
                                    HttpStatus.UNAUTHORIZED.value(),
                                    "Authentication required. Please provide a valid token."
                            );
                            response.getWriter().write(errorJson);
                        })

                        // обработчик на .hasRole эндпоинты
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding("UTF-8");
                            String errorJson = String.format(
                                    "{\"status\": %d, \"message\": \"%s\"}",
                                    HttpStatus.FORBIDDEN.value(),
                                    "Access Denied. You do not have the required permissions."
                            );
                            response.getWriter().write(errorJson);
                        })
                )
                .authorizeHttpRequests(auth -> auth
                        // сваггер
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // поинты auth
                        .requestMatchers("/api/logout").authenticated()
                        .requestMatchers("/api/register", "/api/login",
                                "/api/new-token-pair", "/oauth2/**"
                        ).permitAll()
                        // поинты user
                        .requestMatchers("/api/admin/users/**").hasRole("ADMIN")
                        .requestMatchers("/api/me/**").authenticated()
                        .requestMatchers("/api/users/**").permitAll()
                        // поинты post
                        .requestMatchers("/api/admin/posts/**").hasRole("ADMIN")
                        .requestMatchers("/api/posts/{id}/like").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/posts/**").permitAll()
                        .requestMatchers("/api/posts/**").authenticated()
                        // поинты category
                        .requestMatchers("/api/admin/categories/**").hasRole("ADMIN")
                        .requestMatchers("/api/categories/**").permitAll()
                        // поинты комментариев
                        .requestMatchers(HttpMethod.GET, "/api/posts/{postId}/comments/{commentId}").permitAll()
                        .requestMatchers("/api/posts/{postId}/comments/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/posts/{postId}/comments/**").authenticated()

                        .anyRequest().permitAll()
                )
                .oauth2Login(oauth2 -> {
                    oauth2.successHandler(oAuth2LoginSuccessHandler);
                })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(loggingFilter, BasicAuthenticationFilter.class)
                .addFilterBefore(jwtAuthFilter, BasicAuthenticationFilter.class);

        return http.build();
    }

}