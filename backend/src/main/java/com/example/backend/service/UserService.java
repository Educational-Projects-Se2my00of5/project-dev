package com.example.backend.service;

import com.example.backend.dto.MessageDTO;
import com.example.backend.dto.UserDTO;
import com.example.backend.exception.BadRequestException;
import com.example.backend.exception.NotFoundException;
import com.example.backend.mapper.UserMapper;
import com.example.backend.model.Role;
import com.example.backend.model.User;
import com.example.backend.repository.RoleRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.repository.specification.UserSpecification;
import com.example.backend.util.GetModelOrThrow;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;



@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtProvider jwtProvider;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final GetModelOrThrow getModelOrThrow;

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

    public MessageDTO.Response.GetMessage updatePassword(String authHeader, UserDTO.Request.EditPassword editPassword) {
        User user = getUserFromAuthHeader(authHeader);


        if (user.getPassword() == null || passwordEncoder.matches(editPassword.getOldPassword(), user.getPassword())) {
            String newPassword = passwordEncoder.encode(editPassword.getPassword());

            user.setPassword(newPassword);
            userRepository.save(user);
            return new MessageDTO.Response.GetMessage("success edit password");
        }
        throw new BadRequestException("old password does not right");
    }

    public List<UserDTO.Response.ShortProfile> getUsers() {
        List<User> users = userRepository.findAll();

        return userMapper.toListUsersDTO(users);
    }

    public UserDTO.Response.ShortProfile getUser(Long id) {
        final User user = getModelOrThrow.getUserOrThrow(id);

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

    public UserDTO.Response.FullProfile getFullUser(Long id) {
        final User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        return userMapper.toFullProfileDTO(user);
    }

    public UserDTO.Response.FullProfile editUser(Long id, UserDTO.Request.EditUser editUser) {
        User user = getModelOrThrow.getUserOrThrow(id);

        if (editUser.getUsername() != null) {
            user.setUsername(editUser.getUsername());
        }
        if (editUser.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(editUser.getPassword()));
        }

        user = userRepository.save(user);

        return userMapper.toFullProfileDTO(user);
    }


    @Transactional
    public UserDTO.Response.FullProfile giveRole(Long id, UserDTO.Request.GiveRole giveRole) {
        final User user = getModelOrThrow.getUserOrThrow(id);

        String roleName = giveRole.getRoleName();

        if (roleName.equals("ROLE_ADMIN")) {
            final Role role = roleRepository.findByName("ROLE_ADMIN").orElseThrow();
            if (!user.getRoles().contains(role)) {
                user.getRoles().add(role);
                userRepository.save(user);
                return userMapper.toFullProfileDTO(user);
            }
        } else if (roleName.startsWith("ROLE_MODERATOR_")) {
            try {
                Long categoryId = Long.parseLong(roleName.substring("ROLE_MODERATOR_".length()));
                //Проверка, что есть такая категория
                getModelOrThrow.getCategoryOrThrow(categoryId);

                // Получаем роль или создаём новую
                final Role role = roleRepository.findByName(roleName).orElseGet(() -> {
                    Role newRole = Role.builder().name(roleName).build();
                    return roleRepository.save(newRole);
                });

                if (!user.getRoles().contains(role)) {
                    user.getRoles().add(role);
                    userRepository.save(user);
                    return userMapper.toFullProfileDTO(user);
                }
            } catch (NumberFormatException e) {
                throw new BadRequestException("Роли - " + roleName + ", не существует");
            } catch (NotFoundException e) {
                throw new BadRequestException("Нету категории под эту роль");
            }

        }
        throw new BadRequestException("Пользователь уже имеет данную роль");
    }

}
