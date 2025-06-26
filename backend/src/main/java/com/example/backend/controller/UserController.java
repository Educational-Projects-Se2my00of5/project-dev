package com.example.backend.controller;

import com.example.backend.dto.MessageDTO;
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
@RequestMapping("api")
@RequiredArgsConstructor
public class UserController {

    final String USER_TITLE = "User-controller :: user";
    final String ADMIN_TITLE = "User-controller :: admin";

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Получение профиля", tags = {USER_TITLE}, security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserDTO.Response.FullProfile> getMyProfile(@Parameter(hidden = true) @RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(userService.getMyProfile(authHeader));
    }

    @PutMapping("/me")
    @Operation(summary = "Редактирование профиля", tags = {USER_TITLE}, security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserDTO.Response.FullProfile> updateProfile(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody UserDTO.Request.EditProfile userData) {
        return ResponseEntity.ok(userService.updateProfile(authHeader, userData));
    }

    @PutMapping("/me/password")
    @Operation(summary = "Редактирование пароля", tags = {USER_TITLE}, security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<MessageDTO.Response.GetMessage> updatePassword(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody UserDTO.Request.EditPassword editPassword) {
        return ResponseEntity.ok(userService.updatePassword(authHeader, editPassword));
    }

    @GetMapping("users")
    @Operation(summary = "Получить список всех пользователей с пагинацией и фильтрацией", tags = {USER_TITLE})
    public ResponseEntity<Page<UserDTO.Response.ShortProfile>> getUsers(
            @Parameter(description = "Часть имени пользователя для поиска (нечувствительно к регистру)")
            @RequestParam(required = false) String usernameFilter,
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(userService.getUsers(usernameFilter, pageable));
    }

    @GetMapping("users/{id}")
    @Operation(summary = "Получить пользователя по ID", tags = {USER_TITLE})
    public ResponseEntity<UserDTO.Response.ShortProfile> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUser(id));
    }


    @GetMapping("admin/users/{id}")
    @Operation(summary = "Получение профиля по ID", tags = {ADMIN_TITLE}, security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserDTO.Response.FullProfile> getFullUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getFullUser(id));
    }

    @PutMapping("admin/users/{id}")
    @Operation(summary = "Редактирование профиля по ID", tags = {ADMIN_TITLE}, security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserDTO.Response.FullProfile> editUser(
            @PathVariable Long id,
            @Valid @RequestBody UserDTO.Request.EditUser editUser
    ) {
        return ResponseEntity.ok(userService.editUser(id, editUser));
    }

    @GetMapping("admin/users/{id}/role")
    @Operation(summary = "Назначение роли", tags = {ADMIN_TITLE}, security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserDTO.Response.FullProfile> giveAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(userService.giveAdmin(id));
    }

}
