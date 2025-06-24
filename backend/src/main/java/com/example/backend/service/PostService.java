package com.example.backend.service;

import com.example.backend.dto.PostDTO;
import com.example.backend.exception.ForbiddenException;
import com.example.backend.exception.NotFoundException;
import com.example.backend.mapper.PostMapper;
import com.example.backend.model.Category;
import com.example.backend.model.Post;
import com.example.backend.model.PostLike;
import com.example.backend.model.User;
import com.example.backend.repository.CategoryRepository;
import com.example.backend.repository.PostLikeRepository;
import com.example.backend.repository.PostRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.repository.specification.PostSpecification;
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

@Service
@RequiredArgsConstructor
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostMapper postMapper;
    private final JwtProvider jwtProvider;


    public Page<PostDTO.Response.ShortInfo> getPosts(
            String authHeader,
            Pageable pageable, Long userFilter,
            String titleFilter, Set<Category> categoriesFilter
    ) {
        Long currentUserId = authHeader != null ? jwtProvider.getUserIdFromAuthHeader(authHeader) : null;

        Specification<Post> spec = PostSpecification.byFilters(
                userFilter,
                titleFilter,
                categoriesFilter
        );
        Page<Post> postPage = postRepository.findAll(spec, pageable);

        List<PostDTO.Response.ShortInfo> dtoList = postPage.getContent().stream()
                .map(post -> postMapper.toShortInfoDTO(post, currentUserId))
                .collect(Collectors.toList());

        // Создаем новый объект Page с нашими DTO и информацией о пагинации из старого Page
        return new PageImpl<>(dtoList, pageable, postPage.getTotalElements());
    }

    public PostDTO.Response.FullInfo getPostById(String authHeader, Long id) {
        Long currentUserId = authHeader != null ? jwtProvider.getUserIdFromAuthHeader(authHeader) : null;

        return postMapper.toFullInfoDTO(getPostOrThrow(id), currentUserId);
    }

    public PostDTO.Response.FullInfo createPost(String authHeader, PostDTO.Request.CreatePost post) {
        Long userId = jwtProvider.getUserIdFromAuthHeader(authHeader);
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Set<Category> categories = categoryRepository.findByIdIn(post.getCategoriesId());

        Post newPost = Post
                .builder()
                .author(author)
                .title(post.getTitle())
                .content(post.getContent())
                .categories(categories)
                .build();
        newPost = postRepository.save(newPost);

        return postMapper.toFullInfoDTO(newPost, userId);
    }

    public PostDTO.Response.FullInfo updatePostAdmin(String authHeader, Long id, PostDTO.Request.EditPost editPost) {
        Long userId = jwtProvider.getUserIdFromAuthHeader(authHeader);

        Post post = getPostOrThrow(id);

        Set<Category> categories = categoryRepository.findByIdIn(editPost.getCategoriesId());

        post.setTitle(editPost.getTitle());
        post.setContent(editPost.getContent());
        post.setCategories(categories);

        post = postRepository.save(post);

        return postMapper.toFullInfoDTO(post, userId);
    }

    public PostDTO.Response.GetMessage deletePost(Long id) {
        Post post = getPostOrThrow(id);
        postRepository.delete(post);
        return new PostDTO.Response.GetMessage("success delete post");
    }

    public PostDTO.Response.FullInfo updatePost(String authHeader, Long id, PostDTO.Request.EditPost editPost) {
        String authorId = getPostOrThrow(id).getAuthor().getId().toString();
        String userId = jwtProvider.getUserIdFromAuthHeader(authHeader).toString();

        if (userId.equals(authorId)) {
            return updatePostAdmin(authHeader, id, editPost);
        }
        throw new ForbiddenException("У вас нет прав на редактирование этого поста");
    }

    public PostDTO.Response.GetMessage deletePost(String authHeader, Long id) {
        String authorId = getPostOrThrow(id).getAuthor().getId().toString();
        String userId = (String) jwtProvider.getUserIdFromAuthHeader(authHeader).toString();

        if (userId.equals(authorId)) {
            return deletePost(id);
        }
        throw new ForbiddenException("У вас нет прав на удаление этого поста");
    }


    @Transactional
    public PostDTO.Response.FullInfo likePost(String authHeader, Long postId) {
        Long currentUserId = jwtProvider.getUserIdFromAuthHeader(authHeader);

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Пост не найден"));

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

        return postMapper.toFullInfoDTO(post, currentUserId);
    }

    private Post getPostOrThrow(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пост не найден"));
    }
}
