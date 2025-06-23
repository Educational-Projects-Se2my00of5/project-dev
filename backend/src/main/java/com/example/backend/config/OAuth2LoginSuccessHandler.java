package com.example.backend.config;

import com.example.backend.exception.BadRequestException;
import com.example.backend.model.User;
import com.example.backend.repository.RoleRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtProvider jwtProvider; // Ваш сервис для генерации JWT

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String username = oAuth2User.getAttribute("name");

        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;
        boolean isNewUser;

        if (userOptional.isPresent()) {
            // --- Сценарий: Пользователь уже существует (Вход) ---
            user = userOptional.get();
            isNewUser = false;
        } else {
            // --- Сценарий: Пользователь новый (Регистрация) ---
            isNewUser = true;
            User newUser = User.builder()
                    .email(email)
                    .username(username)
                    .roles(Set.of(roleRepository.findByName("ROLE_USER")
                            .orElseThrow(() -> new BadRequestException("такой роли нет")))
                    )
                    .build();
            user = userRepository.save(newUser);
        }

        // Генерируем JWT для этого пользователя
        final String refreshToken = jwtProvider.generateRefreshToken(user);

        // Перенаправляем на фронтенд с токеном в параметре
        String targetUrl;
        if (isNewUser) {
            targetUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/oauth-success")
                    .queryParam("token", refreshToken)
                    .queryParam("new", "1")
                    .build().toUriString();
        } else {
            targetUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/oauth-success")
                    .queryParam("token", refreshToken)
                    .build().toUriString();
        }

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}