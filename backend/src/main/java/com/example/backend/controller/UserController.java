package com.example.backend.controller;

import com.example.backend.dto.UserDTO;
import com.example.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    @Operation(summary = "Получение профиля", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserDTO.Response.FullProfile> getMyProfile(@Parameter(hidden = true) @RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(userService.getMyProfile(authHeader));
    }

    @PutMapping("/profile")
    @Operation(summary = "Редактирование профиля", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserDTO.Response.FullProfile> updateProfile(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody UserDTO.Request.EditProfile userData) {
        return ResponseEntity.ok(userService.updateProfile(authHeader, userData));
    }

    @PutMapping("/profile/password")
    @Operation(summary = "Редактирование пароля", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserDTO.Response.GetMessage> updatePassword(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody UserDTO.Request.EditPassword editPassword) {
        return ResponseEntity.ok(userService.updatePassword(authHeader, editPassword));
    }

    @GetMapping("/all")
    @Operation(summary = "Получить список всех пользователей с пагинацией и фильтрацией")
    public ResponseEntity<Page<UserDTO.Response.ShortProfile>> getUsers(
            @Parameter(description = "Часть имени пользователя для поиска (нечувствительно к регистру)")
            @RequestParam(required = false) String usernameFilter,
            @ParameterObject Pageable pageable

    ) {
        return ResponseEntity.ok(userService.getUsers(usernameFilter, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить пользователя по ID")
    public ResponseEntity<UserDTO.Response.ShortProfile> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUser(id));
    }


}
