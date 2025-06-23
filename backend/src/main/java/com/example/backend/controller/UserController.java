package com.example.backend.controller;

import com.example.backend.dto.UserDTO;
import com.example.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    @Operation(summary = "Получение своего профиля", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserDTO.Response.FullProfile> getMyProfile(@Parameter(hidden = true) @RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(userService.getMyProfile(authHeader));
    }

    @PutMapping("/profile")
    @Operation(summary = "Редактирование профиля", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserDTO.Response.FullProfile> updateProfile(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody UserDTO.Request.EditProfile userData)
    {
        return ResponseEntity.ok(userService.updateProfile(authHeader, userData));
    }

    @PutMapping("/profile/password")
    @Operation(summary = "Редактирование пароля", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserDTO.Response.GetMessage> updatePassword(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody UserDTO.Request.EditPassword editPassword)
    {
        return ResponseEntity.ok(userService.updatePassword(authHeader, editPassword));
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserDTO.Response.ShortProfile>> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO.Response.ShortProfile> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUser(id));
    }



}
