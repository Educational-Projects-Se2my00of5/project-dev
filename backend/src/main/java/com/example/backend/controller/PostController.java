package com.example.backend.controller;

import com.example.backend.dto.MessageDTO;
import com.example.backend.dto.PostDTO;
import com.example.backend.model.Category;
import com.example.backend.service.PostService;
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

import java.util.Set;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class PostController {

    final String GUEST_TITLE = "Post-controller :: guest";
    final String USER_TITLE = "Post-controller :: user";
    final String ADMIN_TITLE = "Post-controller :: admin";

    private final PostService postService;


    @Operation(summary = "Получить список всех постов с пагинацией и фильтрацией", tags = {GUEST_TITLE})
    @GetMapping("posts")
    public ResponseEntity<Page<PostDTO.Response.ShortInfoPost>> getPosts(
            @Parameter(hidden = true) @RequestHeader(name = "Authorization", required = false) String authHeader,
            @Parameter(description = "Фильтрация по пользователю (id)")
            @RequestParam(required = false) Long userFilter,
            @Parameter(description = "Часть названия поста для поиска (нечувствительно к регистру)")
            @RequestParam(required = false) String titleFilter,
            @Parameter(description = "Категории, для фильтрации, можно указать несколько")
            @RequestParam(required = false) Set<Category> categoriesFilter,
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(postService.getPosts(authHeader, pageable, userFilter, titleFilter, categoriesFilter));
    }

    @Operation(summary = "Получить пост по Id", tags = {GUEST_TITLE})
    @GetMapping("posts/{id}")
    public ResponseEntity<PostDTO.Response.FullInfoPost> getPostById(
            @Parameter(hidden = true) @RequestHeader(name = "Authorization", required = false) String authHeader,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(postService.getPostById(authHeader, id));
    }

    @Operation(
            summary = "Создание поста",
            tags = {USER_TITLE},
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("posts")
    public ResponseEntity<PostDTO.Response.FullInfoPost> createPost(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody PostDTO.Request.CreatePost post
    ) {
        return ResponseEntity.ok(postService.createPost(authHeader, post));
    }

    @Operation(summary = "Редактирование поста",
            tags = {USER_TITLE},
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("posts/{id}")
    public ResponseEntity<PostDTO.Response.FullInfoPost> updatePost(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id,
            @Valid @RequestBody PostDTO.Request.EditPost editPost
    ) {
        return ResponseEntity.ok(postService.updatePost(authHeader, id, editPost));
    }

    @Operation(
            summary = "Удаление поста",
            tags = {USER_TITLE},
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("posts/{id}")
    public ResponseEntity<MessageDTO.Response.GetMessage> deletePost(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(postService.deletePost(authHeader, id));
    }

    @Operation(
            summary = "Поставить/убрать лайк",
            tags = {USER_TITLE},
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("posts/{id}/like")
    public ResponseEntity<PostDTO.Response.FullInfoPost> likePost(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(postService.likePost(authHeader, id));
    }


    @Operation(summary = "Редактирование поста",
            tags = {ADMIN_TITLE},
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("admin/posts/{id}")
    public ResponseEntity<PostDTO.Response.FullInfoPost> updatePostAdmin(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id,
            @Valid @RequestBody PostDTO.Request.EditPost editPost
    ) {
        return ResponseEntity.ok(postService.updatePostAdmin(authHeader, id, editPost));
    }

    @Operation(
            summary = "Удаление поста",
            tags = {ADMIN_TITLE},
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("admin/posts/{id}")
    public ResponseEntity<MessageDTO.Response.GetMessage> deletePost(@PathVariable Long id) {
        return ResponseEntity.ok(postService.deletePost(id));
    }

}
