package com.example.backend.controller;

import com.example.backend.dto.UserDTO;
import com.example.backend.service.AdminService;
import com.example.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/profile/{id}")
    @Operation(summary = "Получение профиля по ID", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserDTO.Response.FullProfile> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getUser(id));
    }
    @PutMapping("/profile/{id}")
    @Operation(summary = "Редактирование профиля по ID", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserDTO.Response.FullProfile> editUser(
            @PathVariable Long id,
            @Valid @RequestBody UserDTO.Request.EditUser editUser
    ) {
        return ResponseEntity.ok(adminService.editUser(id, editUser));
    }

    @GetMapping("/user/{id}/give-role-admin")
    @Operation(summary = "Назначение роли админа", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserDTO.Response.FullProfile> giveAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.giveAdmin(id));
    }

}
