package com.example.backend.mapper;

import com.example.backend.dto.CommentDTO;
import com.example.backend.model.Comment;
import com.example.backend.model.Post;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
@Component
public abstract class CommentMapper {

    @Autowired
    protected UserRepository userRepository;


    // можно указать и просто source = "user", тк только один параметр источник
    @Mapping(target = "authorInfo", source = "comment.user")
    @Mapping(
            target = "parentCommentId",
            expression = "java(comment.getParentComment() != null ? comment.getParentComment().getId() : null)"
    )
    public abstract CommentDTO.Response.InfoComment toInfoCommentDTO(Comment comment);


    public Set<CommentDTO.Response.InfoComment> toInfoCommentDTOSet(Set<Comment> comments, @Context Long currentUserId) {
        if (comments == null) {
            return Collections.emptySet();
        }
        return comments.stream()
                .map(comment -> chooseMapToInfoDto(comment, currentUserId)) // Применяем основной метод к каждому элементу
                .collect(Collectors.toSet());
    }

    public CommentDTO.Response.InfoComment chooseMapToInfoDto(Comment comment, @Context Long currentUserId) {
        if (comment == null) {
            return null;
        }

        User currentUser = null;
        boolean isModeratorCategory = false;
        boolean isAdmin = false;
        if (currentUserId != null) {
           currentUser = userRepository.findById(currentUserId).orElse(null);

            // Определяем, является ли текущий пользователь администратором
            isAdmin = currentUser != null && currentUser.getRoles().stream()
                    .anyMatch(role -> "ROLE_ADMIN".equals(role.getName()));


            Post post = comment.getPost();
            Set<Long> moderatedCategoryIds = currentUser.getRoles()
                    .stream()
                    .filter(role -> role.getName().startsWith("ROLE_MODERATOR_"))
                    .map(role -> Long.parseLong(role.getName().substring("ROLE_MODERATOR_".length())))
                    .collect(Collectors.toSet());
            // Определяем, является ли текущий пользователь модератором
            // хотя бы одной из категорий поста
            isModeratorCategory = post.getCategories().stream()
                    .anyMatch(postCategory -> moderatedCategoryIds.contains(postCategory.getId()));

        }

        // Если комментарий "удален", пользователь не админ и не модератор, тогда возвращаем "пустышку"
        if (comment.isDeleted() && !isAdmin && !isModeratorCategory) {
            return createDeletedComment(comment);
        } else {
            // В противном случае возвращаем полную информацию
            return toInfoCommentDTO(comment);
        }
    }

    private CommentDTO.Response.InfoComment createDeletedComment(Comment comment) {
        // Создаем DTO вручную, заполняя только необходимые поля
        return new CommentDTO.Response.InfoComment(
                comment.getId(),              // id
                "[Комментарий удален]",       // body
                null,                         // authorInfo
                null,                         // createdAt
                null,                         // updatedAt
                comment.getParentComment()==null?null:comment.getParentComment().getId(),
                true                          // deleted
        );
    }
}

