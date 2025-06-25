package com.example.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.Set;

public enum CommentDTO {
    ;

    public enum Request {
        ;

        @Value
        public static class CreateComment implements Body, ParentCommentId {
            @NotBlank(message = "Текст комментария не может быть пустым")
            String body;

            Long parentCommentId;
        }

        @Value
        public static class UpdateComment implements Body {
            @NotBlank(message = "Текст комментария не может быть пустым")
            String body;
        }
    }

    public enum Response {
        ;

        @Value
        public static class InfoComment implements Id, Body, AuthorInfo, CreateAt {
            Long id;

            String body;

            UserDTO.Response.ShortProfile authorInfo;

            LocalDateTime createdAt;
        }
    }

    private interface Id {
        @NotNull
        @Positive
        @Schema(description = "ID комментария", example = "101")
        Long getId();
    }

    private interface Body {
        @Schema(description = "Текст комментария", example = "Отличный пост, спасибо!")
        String getBody();
    }

    private interface AuthorInfo {
        @NotNull
        @Schema(description = "Информация об авторе комментария")
        UserDTO.Response.ShortProfile getAuthorInfo();
    }

    private interface CreateAt {
        @NotNull
        @Schema(description = "Дата и время создания комментария")
        LocalDateTime getCreatedAt();
    }

    private interface UpdateAt {
        @NotNull
        @Schema(description = "Дата и время последнего изменения комментария")
        LocalDateTime getUpdatedAt();
    }

    private interface Replies {
        Set<Response.InfoComment> getReplies();
    }

    private interface ParentCommentId {
        Long getParentCommentId();
    }
}