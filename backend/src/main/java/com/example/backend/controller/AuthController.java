package com.example.backend.controller;


import com.example.backend.dto.UserDTO;
import com.example.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
@Tag(name = "Auth-controller")
public class AuthController {

    private final AuthService authService;

    @PostMapping("login")
    @Operation(summary = "Логин")
    public ResponseEntity<UserDTO.Response.TokenAndShortUserInfo> login(
            @Valid @RequestBody UserDTO.Request.Login authRequest
    ) {
        return ResponseEntity.ok(authService.login(authRequest));
    }

    @PostMapping("register")
    @Operation(summary = "Регистрация")
    public ResponseEntity<UserDTO.Response.TokenAndShortUserInfo> register(
            @Valid @RequestBody UserDTO.Request.Register authRequest
    ) {
        return ResponseEntity.ok(authService.registration(authRequest));
    }

    @PostMapping("new-token-pair")
    @Operation(summary = "Получение новой пары токенов с помощью refresh токена")
    public ResponseEntity<UserDTO.Response.PairTokens> getNewTokenPair(@Valid @RequestBody UserDTO.Request.RefreshToken token) {
        return ResponseEntity.ok(authService.getNewTokenPair(token));
    }

    @PostMapping("logout")
    @Operation(summary = "Выход из аккаунта", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserDTO.Response.GetMessage> logout(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody UserDTO.Request.RefreshToken token
    ) {
        return ResponseEntity.ok(authService.logout(authHeader, token));
    }


    @Operation(
            summary = "Инициировать вход через Google",
            description = "Перенаправляет пользователя на страницу аутентификации Google. " +
                    "Это не REST API вызов, а точка для редиректа браузера. " +
                    "После успешного входа Google перенаправит пользователя на callback URL, " +
                    "а бэкенд, в свою очередь, на фронтенд с JWT-токеном в параметре. " +
                    "http://localhost:3000/oauth-success"
    )
    @ApiResponse(
            responseCode = "302",
            description = "Found - Происходит редирект на страницу входа Google."
    )
    @GetMapping("/oauth2/authorization/google")
    public void googleAuthRedirect() {}
}
