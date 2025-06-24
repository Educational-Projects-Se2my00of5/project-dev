package com.example.backend.mapper;

import com.example.backend.dto.PostDTO;
import com.example.backend.dto.UserDTO;
import com.example.backend.model.Post;
import com.example.backend.model.User;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        uses = { UserMapper.class }
)
public interface PostMapper {
    // Добавляем @Context Long currentUserId в сигнатуру
    @Mapping(target = "shortUserInfo", source = "post.author")
    // Временно игнорируем поля, которые вычислим позже
    @Mapping(target = "likes", ignore = true)
    @Mapping(target = "hasLiked", ignore = true)
    PostDTO.Response.FullInfo toFullInfoDTO(Post post, @Context Long currentUserId);

    // Этот метод тоже нужно будет доработать аналогично, если он тоже должен возвращать эти поля
    @Mapping(target = "shortUserInfo", source = "post.author")
    @Mapping(target = "likes", ignore = true)
    @Mapping(target = "hasLiked", ignore = true)
    PostDTO.Response.ShortInfo toShortInfoDTO(Post post, @Context Long currentUserId);


    @AfterMapping
    default void completePostFullInfoDTO(Post post, @MappingTarget PostDTO.Response.FullInfo dto, @Context Long currentUserId) {
        if (post == null || post.getLikes() == null) {
            dto.setLikes(0L);
            dto.setHasLiked(false);
            return;
        }

        // 1. Вычисляем количество лайков
        long likeCount = post.getLikes().size();
        dto.setLikes(likeCount);

        // 2. Проверяем, лайкнул ли текущий пользователь
        // Если currentUserId не передан (анонимный пользователь), считаем, что он не лайкал
        if (currentUserId == null) {
            dto.setHasLiked(false);
            return;
        }

        boolean hasLiked = post.getLikes().stream()
                .anyMatch(like -> like.getUser().getId().equals(currentUserId));
        dto.setHasLiked(hasLiked);
    }

    @AfterMapping
    default void completePostShortInfoDTO(Post post, @MappingTarget PostDTO.Response.ShortInfo dto, @Context Long currentUserId) {
        if (post == null || post.getLikes() == null) {
            dto.setLikes(0L);
            dto.setHasLiked(false);
            return;
        }

        // 1. Вычисляем количество лайков
        long likeCount = post.getLikes().size();
        dto.setLikes(likeCount);

        // 2. Проверяем, лайкнул ли текущий пользователь
        // Если currentUserId не передан (анонимный пользователь), считаем, что он не лайкал
        if (currentUserId == null) {
            dto.setHasLiked(false);
            return;
        }

        boolean hasLiked = post.getLikes().stream()
                .anyMatch(like -> like.getUser().getId().equals(currentUserId));
        dto.setHasLiked(hasLiked);
    }

}
