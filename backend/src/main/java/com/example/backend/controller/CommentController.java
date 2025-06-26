package com.example.backend.controller;


import com.example.backend.dto.CommentDTO;
import com.example.backend.dto.MessageDTO;
import com.example.backend.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("api/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    final String GUEST_TITLE = "Comment-controller :: guest";
    final String USER_TITLE = "Comment-controller :: user";
    final String ADMIN_TITLE = "Comment-controller :: admin";


    private final CommentService commentService;

    // Получение комментариев будет частью эндпоинта получения поста,
    // поэтому отдельный GET-метод здесь не всегда нужен.
    // Если все же нужен, его можно добавить.

    @GetMapping("{commentId}")
    @Operation(
            summary = "Раскрыть ответы к коментарию",
            tags = {GUEST_TITLE},
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<Set<CommentDTO.Response.InfoComment>> getReplies(@PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.getReplies(commentId));
    }

    @PostMapping
    @Operation(
            summary = "Создать новый комментарий к посту",
            tags = {USER_TITLE},
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<CommentDTO.Response.InfoComment> createComment(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader,
            @PathVariable Long postId,
            @Valid @RequestBody CommentDTO.Request.CreateComment comment
    ) {
        return ResponseEntity.ok(commentService.createComment(authHeader, postId, comment));
    }

    @PutMapping("{commentId}")
    @Operation(
            summary = "Редактировать свой комментарий",
            tags = {USER_TITLE},
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<CommentDTO.Response.InfoComment> updateComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentDTO.Request.UpdateComment comment
    ) {
        return ResponseEntity.ok(commentService.updateComment(commentId, comment));
    }


    // Возможно сделаю чтобы комменты не удалялись из бд, а становились невидимы обычным пользователям(и создателю тож)
    // но не сегодня

    @DeleteMapping("{commentId}")
    @Operation(
            summary = "Удалить свой комментарий",
            tags = {USER_TITLE},
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<MessageDTO.Response.GetMessage> deleteMyComment(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader,
            @PathVariable Long postId,
            @PathVariable Long commentId
    ) {

        return ResponseEntity.ok(commentService.deleteMyComment(commentId, authHeader));
    }

    @DeleteMapping("admin/{commentId}")
    @Operation(
            summary = "Удалить комментарий",
            tags = {ADMIN_TITLE},
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<MessageDTO.Response.GetMessage> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId
    ) {
        return ResponseEntity.ok(commentService.deleteComment(commentId));
    }
}
