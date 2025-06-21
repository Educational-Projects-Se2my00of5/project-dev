package com.example.backend.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    




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
