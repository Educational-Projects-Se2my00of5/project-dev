package com.example.backend.service;


import com.example.backend.dto.CommentDTO;
import com.example.backend.dto.MessageDTO;
import com.example.backend.exception.BadRequestException;
import com.example.backend.exception.ForbiddenException;
import com.example.backend.mapper.CommentMapper;
import com.example.backend.model.Comment;
import com.example.backend.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

import static com.example.backend.util.GetModelOrThrow.*;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final JwtProvider jwtProvider;


    public Set<CommentDTO.Response.InfoComment> getReplies(Long commentId) {
        Comment parentComment = getCommentOrThrow(commentId);

        return parentComment.getReplies().stream()
                .map(commentMapper::toInfoCommentDTO)
                .collect(Collectors.toSet());
    }

    public CommentDTO.Response.InfoComment createComment(String authHeader, Long postId, CommentDTO.Request.CreateComment comment) {
        Long userId = jwtProvider.getUserIdFromAuthHeader(authHeader);

        Comment newComment = Comment
                .builder()
                .user(getUserOrThrow(userId))
                .post(getPostOrThrow(postId))
                .body(comment.getBody())
                .build();

        // Логика для вложенных комментариев
        if (comment.getParentCommentId() != null) {
            Comment parentComment = getCommentOrThrow(comment.getParentCommentId());

            // Проверка, что родительский комментарий принадлежит тому же посту
            if (!parentComment.getPost().getId().equals(postId)) {
                throw new BadRequestException("Ответ можно оставлять только на комментарии этого же поста.");
            }

            newComment.setParentComment(parentComment);
        }

        Comment savedComment = commentRepository.save(newComment);
        return commentMapper.toInfoCommentDTO(savedComment);
    }

    public CommentDTO.Response.InfoComment updateComment(Long commentId, CommentDTO.Request.UpdateComment comment) {
        Comment commentToUpdate = getCommentOrThrow(commentId);

        commentToUpdate.setBody(comment.getBody());

        Comment updatedComment = commentRepository.save(commentToUpdate);

        return commentMapper.toInfoCommentDTO(updatedComment);
    }


    public MessageDTO.Response.GetMessage deleteMyComment(Long commentId, String authHeader) {
        Long currentUserId = jwtProvider.getUserIdFromAuthHeader(authHeader);
        Comment commentToDelete = getCommentOrThrow(commentId);

        // проверка
        if (!commentToDelete.getUser().getId().equals(currentUserId)) {
            throw new ForbiddenException("Вы не можете удалить чужой комментарий.");
        }

        commentRepository.delete(commentToDelete);
        return new MessageDTO.Response.GetMessage("success delete comment");
    }

    public MessageDTO.Response.GetMessage deleteComment(Long commentId) {
        Comment commentToDelete = getCommentOrThrow(commentId);

        commentRepository.delete(commentToDelete);
        return new MessageDTO.Response.GetMessage("success delete comment");
    }

}
