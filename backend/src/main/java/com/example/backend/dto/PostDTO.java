package com.example.backend.dto;

import com.example.backend.model.Category;
import com.example.backend.model.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.Set;

public enum PostDTO {
    ;

    public enum Request {
        ;

        @Value
        public static class CreatePost implements Title, Content, CategoriesId {

            @NotBlank
            String title;

            @NotBlank
            String content;

            @NotBlank
            Set<Long> categoriesId;
        }

        @Value
        public static class EditPost implements Title, Content, CategoriesId {

            @NotBlank
            String title;

            @NotBlank
            String content;

            @NotBlank
            Set<Long> categoriesId;
        }

    }

    public enum Response {
        ;

        @Data
        public static class FullInfo implements Id, ShortUserInfo, Title, Content, Likes, HasLiked, CreateAt, UpdateAt, Categories, Comments {

            @NotNull
            Long id;

            @NotNull
            UserDTO.Response.ShortProfile shortUserInfo;

            @NotBlank
            String title;

            @NotBlank
            String content;

            @NotNull
            Long likes;

            @NotNull
            Boolean hasLiked;

            @NotNull
            LocalDateTime createdAt;

            @NotNull
            LocalDateTime updatedAt;

            @NotNull
            Set<Category> categories;

            @NotNull
            Set<Comment> comments;
        }

        @Data
        public static class ShortInfo implements Id, ShortUserInfo, Title, Likes, HasLiked, CreateAt, Categories {

            @NotNull
            Long id;

            @NotBlank
            UserDTO.Response.ShortProfile shortUserInfo;

            @NotBlank
            String title;

            @NotNull
            Long likes;

            @NotNull
            Boolean hasLiked;

            @NotNull
            LocalDateTime createdAt;

            @NotNull
            Set<Category> categories;
        }

        @Value
        public static class GetMessage implements Message {
            String message;
        }
    }

    private interface Id {
        @Positive
        @Schema(description = "ID поста", example = "1")
        Long getId();
    }

    private interface ShortUserInfo {
        @Schema(description = "Id пользователя", example = "1")
        UserDTO.Response.ShortProfile getShortUserInfo();
    }

    private interface Title {
        @Schema(description = "Название", example = "Новый пост")
        String getTitle();
    }

    private interface Content {
        @Schema(description = "Содержание поста", example = "Этот пост посвящён....")
        @Size(max = 2000, message = "Максимум 2000 символов")
        String getContent();
    }

    private interface Likes {
        @Schema(description = "Кол-во лайков", example = "1223")
        Long getLikes();
    }
    private interface HasLiked {
        @Schema(description = "Лайкнут ли пост конкретным юзером")
        Boolean getHasLiked();
    }

    private interface CreateAt {
        @Schema(description = "Время создания")
        LocalDateTime getCreatedAt();
    }

    private interface UpdateAt {
        @Schema(description = "Время последнего изменения")
        LocalDateTime getUpdatedAt();
    }

    private interface Comments {
        @Schema(description = "Комментарии")
        Set<Comment> getComments();
    }

    private interface CategoriesId {
        @Schema(description = "Id категорий в которые входит пост")
        Set<Long> getCategoriesId();
    }

    private interface Categories {
        @Schema(description = "Категории в которые входит пост")
        Set<Category> getCategories();
    }

    private interface Message {
        @Schema(description = "Сообщение", example = "Операция выполнена успешно")
        String getMessage();
    }
}