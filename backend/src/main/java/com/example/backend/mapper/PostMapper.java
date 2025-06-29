package com.example.backend.mapper;

import com.example.backend.dto.PostDTO;
import com.example.backend.model.Comment;
import com.example.backend.model.Post;
import com.example.backend.model.User;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.*;

import java.util.Optional;
import java.util.Set;


@Mapper(
        componentModel = "spring",
        uses = {UserMapper.class, CategoryMapper.class, CommentMapper.class}
)
public interface PostMapper {

    @Mapping(target = "shortUserInfo", source = "post.author")
    @Mapping(target = "categories", source = "post.categories")
    // комменты должны замаппиться в CommentMapper, пж
    @Mapping(target = "rootComments", source = "rootComments")
    @Mapping(target = "likes", ignore = true)
    @Mapping(target = "hasLiked", ignore = true)
    PostDTO.Response.FullInfoPost toFullInfoDTO(
            Post post,
            Set<Comment> rootComments,
            @Context Long currentUserId
    );

    @Mapping(target = "shortUserInfo", source = "post.author")
    @Mapping(target = "categories", source = "post.categories")
    @Mapping(target = "likes", ignore = true)
    @Mapping(target = "hasLiked", ignore = true)
    PostDTO.Response.ShortInfoPost toShortInfoDTO(Post post, @Context Long currentUserId);


    @AfterMapping
    default void completePostFullInfoDTO(
            Post post,
            @MappingTarget PostDTO.Response.FullInfoPost dto,
            @Context Long currentUserId
    ) {
        if (post == null) return;

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
    default void completePostShortInfoDTO(
            Post post,
            @MappingTarget PostDTO.Response.ShortInfoPost dto,
            @Context Long currentUserId
    ) {
        if (post == null) return;


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
