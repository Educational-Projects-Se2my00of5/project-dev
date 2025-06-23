package com.example.backend.service;


import com.example.backend.dto.UserDTO;
import com.example.backend.exception.AuthenticationException;
import com.example.backend.exception.BadRequestException;
import com.example.backend.exception.NotFoundException;
import com.example.backend.mapper.UserMapper;
import com.example.backend.model.User;
import com.example.backend.repository.RoleRepository;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final RedisProvider redisProvider;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    public UserDTO.Response.TokenAndShortUserInfo login(
            UserDTO.Request.Login authRequest
    ) {
        final User user = userRepository.findByEmail(authRequest.getEmail())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (user.getPassword() == null) {
            throw new AuthenticationException("Вход возможен только через Google");
        } else {
            // сравниваем сырой и хэшированный пароль
            if (passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
                final String refreshToken = jwtProvider.generateRefreshToken(user);
                redisProvider.addToken(user.getId().toString(), refreshToken, jwtProvider.getJwtRefreshExpiration());

                return userMapper.toTokenAndShortUserInfoDTO(user, refreshToken);
            } else {
                throw new AuthenticationException("Пароли не совпадают");
            }
        }
    }

    @Transactional
    public UserDTO.Response.TokenAndShortUserInfo registration(
            @NonNull UserDTO.Request.Register userData
    ) {
        if (userRepository.findByEmail(userData.getEmail()).isPresent()) {
            throw new BadRequestException("этот email уже занят");
        }

        User user = User.builder()
                .username(userData.getUsername())
                .email(userData.getEmail())
                .password(passwordEncoder.encode(userData.getPassword()))
                .roles(Set.of(roleRepository.findByName("ROLE_USER")
                        .orElseThrow(() -> new BadRequestException("такой роли нет")))
                )
                .build();
        userRepository.save(user);

        final String refreshToken = jwtProvider.generateRefreshToken(user);
        redisProvider.addToken(user.getId().toString(), refreshToken, jwtProvider.getJwtRefreshExpiration());

        return userMapper.toTokenAndShortUserInfoDTO(user, refreshToken);
    }

    public UserDTO.Response.PairTokens getNewTokenPair(UserDTO.Request.RefreshToken token) {
        String refreshToken = token.getRefreshToken();
        String userIdFromDB = redisProvider.findUserIdByToken(refreshToken);

        if (jwtProvider.validateRefreshToken(refreshToken) && userIdFromDB != null) {
            final String email = jwtProvider.getRefreshClaims(refreshToken).getSubject();
            final User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AuthenticationException("invalid token"));

            final String userId = user.getId().toString();

            if (userId.equals(userIdFromDB)) {
                redisProvider.deleteToken(refreshToken);
                refreshToken = jwtProvider.generateRefreshToken(user);
                redisProvider.addToken(user.getId().toString(), refreshToken, jwtProvider.getJwtRefreshExpiration());

                return userMapper.toPairTokensDTO(
                        jwtProvider.generateAccessToken(user),
                        refreshToken
                );
            }
        }
        throw new BadRequestException("invalid refresh token");
    }

    public UserDTO.Response.GetMessage logout(String authHeader, UserDTO.Request.RefreshToken token) {
        String refreshToken = token.getRefreshToken();
        String accessToken = jwtProvider.getTokenFromAuthHeader(authHeader);

        if (jwtProvider.validateRefreshToken(refreshToken)) {
            redisProvider.deleteToken(refreshToken);
            redisProvider.addToken(accessToken, "not-valid", jwtProvider.getJwtAccessExpiration());

            return new UserDTO.Response.GetMessage("success");
        } else {
            throw new BadRequestException("invalid refresh token");
        }

    }

}
