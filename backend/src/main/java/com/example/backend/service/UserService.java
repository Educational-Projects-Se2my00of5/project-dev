package com.example.backend.service;

import com.example.backend.dto.UserDTO;
import com.example.backend.exception.BadRequestException;
import com.example.backend.exception.NotFoundException;
import com.example.backend.mapper.UserMapper;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.repository.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserDTO.Response.FullProfile getMyProfile(String authHeader) {
        final User user = getUserFromAuthHeader(authHeader);

        return userMapper.toFullProfileDTO(user);
    }

    public UserDTO.Response.FullProfile updateProfile(String authHeader, UserDTO.Request.EditProfile userData) {
        User user = getUserFromAuthHeader(authHeader);

        // При добавлении новых полей добавить и сюда
        if (userData.getUsername() != null) {
            user.setUsername(userData.getUsername());
        }

        userRepository.save(user);

        return userMapper.toFullProfileDTO(user);
    }

    public UserDTO.Response.GetMessage updatePassword(String authHeader, UserDTO.Request.EditPassword editPassword) {

        User user = getUserFromAuthHeader(authHeader);

        if (passwordEncoder.matches(editPassword.getOldPassword(), user.getPassword())) {
            String newPassword = passwordEncoder.encode(editPassword.getPassword());

            user.setPassword(newPassword);
            userRepository.save(user);
            return new UserDTO.Response.GetMessage("success edit password");
        }
        throw new BadRequestException("old password does not right");
    }

    public List<UserDTO.Response.ShortProfile> getUsers() {
        List<User> users = userRepository.findAll();

        return userMapper.toListUsersDTO(users);
    }

    public UserDTO.Response.ShortProfile getUser(Long id) {
        final User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        return userMapper.toShortProfileDTO(user);
    }


    private User getUserFromAuthHeader(String authHeader) {
        String email = jwtProvider.getEmailFromAuthHeader(authHeader);

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    public Page<UserDTO.Response.ShortProfile> getUsers(String usernameFilter, Pageable pageable) {

        Specification<User> spec = UserSpecification.usernameContains(usernameFilter);

        Page<User> userPage = userRepository.findAll(spec, pageable);

        return userPage.map(userMapper::toShortProfileDTO);
    }
}
