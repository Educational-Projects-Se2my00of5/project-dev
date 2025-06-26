package com.example.backend.util;

import com.example.backend.exception.NotFoundException;
import com.example.backend.model.Category;
import com.example.backend.model.Comment;
import com.example.backend.model.Post;
import com.example.backend.model.User;
import com.example.backend.repository.CategoryRepository;
import com.example.backend.repository.CommentRepository;
import com.example.backend.repository.PostRepository;
import com.example.backend.repository.UserRepository;

public class GetModelOrThrow {
    private static PostRepository postRepository;
    private static UserRepository userRepository;
    private static CategoryRepository categoryRepository;
    private static CommentRepository commentRepository;


    public static Comment getCommentOrThrow(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден"));
    }
    public static Post getPostOrThrow(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пост не найден"));
    }
    public static User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    public static Category getCategoryOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Категория не найдена"));
    }
}
