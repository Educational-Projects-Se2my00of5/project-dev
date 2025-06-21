package com.example.backend.config;

import com.example.backend.exception.BadRequestException;
import com.example.backend.model.RefreshToken;
import com.example.backend.model.User;
import com.example.backend.repository.RefreshTokenRepository;
import com.example.backend.repository.RoleRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider; // Ваш сервис для генерации JWT

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String username = oAuth2User.getAttribute("name");

        // Логика "Найти или создать"
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    // Создаем нового пользователя, если его нет
                    User newUser = User
                            .builder()
                            .email(email)
                            .username(username)
                            .roles(Set.of(roleRepository.findByName("ROLE_USER")
                                    .orElseThrow(() -> new BadRequestException("такой роли нет")))
                            )
                            .build();
                    return userRepository.save(newUser);
                });

        // Генерируем JWT для этого пользователя
        final String accessToken = jwtProvider.generateAccessToken(user);
        final String refreshToken = jwtProvider.generateRefreshToken(user);
        refreshTokenRepository.save(new RefreshToken(refreshToken));

        // Перенаправляем на фронтенд с токеном в параметре
        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/oauth-success") // Адрес страницы на фронте
                .queryParam("token", refreshToken)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}