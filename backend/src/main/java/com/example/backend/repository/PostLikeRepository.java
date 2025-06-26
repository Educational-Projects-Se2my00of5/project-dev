package com.example.backend.repository;

import com.example.backend.model.Post;
import com.example.backend.model.PostLike;
import com.example.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    // Метод для поиска лайка по пользователю и посту
    Optional<PostLike> findByUserAndPost(User user, Post post);

    // Метод для подсчета лайков у поста
    Long countByPost(Post post);

    // Метод для проверки, лайкнул ли пользователь пост
    boolean existsByUserAndPost(User user, Post post);
}
