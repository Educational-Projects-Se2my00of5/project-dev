package com.example.backend.service;

import com.example.backend.dto.MessageDTO;
import com.example.backend.dto.PostDTO;
import com.example.backend.exception.ForbiddenException;
import com.example.backend.mapper.PostMapper;
import com.example.backend.model.*;
import com.example.backend.repository.CategoryRepository;
import com.example.backend.repository.CommentRepository;
import com.example.backend.repository.PostLikeRepository;
import com.example.backend.repository.PostRepository;
import com.example.backend.repository.specification.PostSpecification;
import com.example.backend.util.GetModelOrThrow;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.backend.util.GetModelOrThrow.*;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CategoryRepository categoryRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostMapper postMapper;
    private final JwtProvider jwtProvider;
    private final GetModelOrThrow getModelOrThrow;

    public Page<PostDTO.Response.ShortInfoPost> getPosts(
            String authHeader,
            Pageable pageable, Long userFilter,
            String titleFilter, Set<Category> categoriesFilter
    ) {
        Long currentUserId = authHeader != null ? jwtProvider.getUserIdFromAuthHeader(authHeader) : null;

        Optional<User> currentUser = getModelOrThrow.getOptionalUser(currentUserId);

        Specification<Post> spec = PostSpecification.byFilters(
                userFilter,
                titleFilter,
                categoriesFilter
        );
        Page<Post> postPage = postRepository.findAll(spec, pageable);

        List<PostDTO.Response.ShortInfoPost> dtoList = postPage.getContent().stream()
                .map(post -> postMapper.toShortInfoDTO(post, currentUser))
                .collect(Collectors.toList());

        // Создаем новый объект Page с нашими DTO и информацией о пагинации из старого Page
        return new PageImpl<>(dtoList, pageable, postPage.getTotalElements());
    }

    public PostDTO.Response.FullInfoPost getPostById(String authHeader, Long id) {
        Long currentUserId = authHeader != null ? jwtProvider.getUserIdFromAuthHeader(authHeader) : null;

        Optional<User> currentUser = getModelOrThrow.getOptionalUser(currentUserId);
        Post post = getModelOrThrow.getPostOrThrow(id);

        Set<Comment> rootComments = commentRepository.findByPostAndParentCommentIsNull(post);

        return postMapper.toFullInfoDTO(post, rootComments, currentUser);
    }

    public PostDTO.Response.FullInfoPost createPost(String authHeader, PostDTO.Request.CreatePost post) {
        Long userId = jwtProvider.getUserIdFromAuthHeader(authHeader);
        User author = getModelOrThrow.getUserOrThrow(userId);

        Set<Category> categories = categoryRepository.findByIdIn(post.getCategoriesId());

        Post newPost = Post
                .builder()
                .author(author)
                .title(post.getTitle())
                .content(post.getContent())
                .categories(categories)
                .build();
        newPost = postRepository.save(newPost);

        return postMapper.toFullInfoDTO(newPost, Set.of(), Optional.of(author));
    }

    public PostDTO.Response.FullInfoPost updatePostAdmin(String authHeader, Long id, PostDTO.Request.EditPost editPost) {
        Long userId = jwtProvider.getUserIdFromAuthHeader(authHeader);

        Optional<User> currentUser = getModelOrThrow.getOptionalUser(userId);
        Post post = getModelOrThrow.getPostOrThrow(id);

        Set<Category> categories = categoryRepository.findByIdIn(editPost.getCategoriesId());

        post.setTitle(editPost.getTitle());
        post.setContent(editPost.getContent());
        post.setCategories(categories);

        post = postRepository.save(post);

        Set<Comment> rootComments = commentRepository.findByPostAndParentCommentIsNull(post);

        return postMapper.toFullInfoDTO(post, rootComments, currentUser);
    }

    public MessageDTO.Response.GetMessage deletePost(Long id) {
        Post post = getModelOrThrow.getPostOrThrow(id);
        postRepository.delete(post);
        return new MessageDTO.Response.GetMessage("success delete post");
    }

    public PostDTO.Response.FullInfoPost updatePost(String authHeader, Long id, PostDTO.Request.EditPost editPost) {
        String authorId = getModelOrThrow.getPostOrThrow(id).getAuthor().getId().toString();
        String userId = jwtProvider.getUserIdFromAuthHeader(authHeader).toString();

        if (userId.equals(authorId)) {
            return updatePostAdmin(authHeader, id, editPost);
        }
        throw new ForbiddenException("У вас нет прав на редактирование этого поста");
    }

    public MessageDTO.Response.GetMessage deletePost(String authHeader, Long id) {
        String authorId = getModelOrThrow.getPostOrThrow(id).getAuthor().getId().toString();
        String userId = jwtProvider.getUserIdFromAuthHeader(authHeader).toString();

        if (userId.equals(authorId)) {
            return deletePost(id);
        }
        throw new ForbiddenException("У вас нет прав на удаление этого поста");
    }


    @Transactional
    public PostDTO.Response.FullInfoPost likePost(String authHeader, Long postId) {
        Long currentUserId = jwtProvider.getUserIdFromAuthHeader(authHeader);

        User currentUser = getModelOrThrow.getUserOrThrow(currentUserId);
        Post post = getModelOrThrow.getPostOrThrow(postId);

        Optional<PostLike> existingLike = postLikeRepository.findByUserAndPost(currentUser, post);

        if (existingLike.isPresent()) {
            postLikeRepository.delete(existingLike.get());
            // Также нужно удалить из коллекции в посте, чтобы размер был корректным
            post.getLikes().remove(existingLike.get());
        } else {
            PostLike newLike = PostLike.builder()
                    .user(currentUser)
                    .post(post)
                    .build();
            postLikeRepository.save(newLike);
            // Добавляем новый лайк в коллекцию поста, чтобы не запрашивать пост снова
            post.getLikes().add(newLike);
        }

        Set<Comment> rootComments = commentRepository.findByPostAndParentCommentIsNull(post);

        return postMapper.toFullInfoDTO(post, rootComments, Optional.of(currentUser));
    }

}
