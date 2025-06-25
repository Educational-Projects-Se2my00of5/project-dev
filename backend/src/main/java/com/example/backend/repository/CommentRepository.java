package com.example.backend.repository;

import com.example.backend.model.Comment;
import com.example.backend.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Set<Comment> findByPostAndParentCommentIsNull(Post post);
}
