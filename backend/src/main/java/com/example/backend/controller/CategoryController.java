package com.example.backend.controller;


import com.example.backend.dto.CategoryDTO;
import com.example.backend.dto.MessageDTO;
import com.example.backend.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/categories")
@RequiredArgsConstructor
public class CategoryController {

    final String GUEST_TITLE = "Category-controller :: guest";
    final String USER_TITLE = "Category-controller :: user";
    final String ADMIN_TITLE = "Category-controller :: admin";

    private final CategoryService categoryService;

    // --- Публичные эндпоинты ---

    @GetMapping("/{id}")
    @Operation(summary = "Получить категорию по id", tags = {GUEST_TITLE})
    public ResponseEntity<CategoryDTO.Response.FullInfoCategory> getCategory(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategory(id));
    }

    @GetMapping
    @Operation(summary = "Получить список всех категорий", tags = {GUEST_TITLE})
    public ResponseEntity<List<CategoryDTO.Response.FullInfoCategory>> getAllCategories(
            @RequestParam(required = false) String nameFilter
    ) {
        return ResponseEntity.ok(categoryService.getAllCategories(nameFilter));
    }

    // --- Эндпоинты для администратора ---

    @PostMapping
    @Operation(
            summary = "Создать новую категорию (только для админа)",
            tags = {ADMIN_TITLE},
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<CategoryDTO.Response.FullInfoCategory> createCategory(
            @Valid @RequestBody CategoryDTO.Request.CreateOrUpdateCategory category
    ) {
        return ResponseEntity.ok(categoryService.createCategory(category));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Обновить существующую категорию (только для админа)",
            tags = {ADMIN_TITLE},
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<CategoryDTO.Response.FullInfoCategory> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryDTO.Request.CreateOrUpdateCategory updateDto
    ) {
        return ResponseEntity.ok(categoryService.updateCategory(id, updateDto));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Удалить категорию (только для админа)",
            tags = {ADMIN_TITLE},
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<MessageDTO.Response.GetMessage> deleteCategory(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.deleteCategory(id));
    }
}

