package com.example.backend.service;

import com.example.backend.dto.UserDTO;
import com.example.backend.exception.BadRequestException;
import com.example.backend.exception.NotFoundException;
import com.example.backend.mapper.UserMapper;
import com.example.backend.model.Role;
import com.example.backend.model.User;
import com.example.backend.repository.RoleRepository;
import com.example.backend.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;


    public UserDTO.Response.FullProfile getUser(Long id) {
        final User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        return userMapper.toFullProfileDTO(user);
    }

    public UserDTO.Response.FullProfile editUser(Long id, UserDTO.Request.EditUser editUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if(editUser.getUsername() != null) {
            user.setUsername(editUser.getUsername());
        }
        if(editUser.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(editUser.getPassword()));
        }

        user = userRepository.save(user);

        return userMapper.toFullProfileDTO(user);
    }

    public UserDTO.Response.FullProfile giveAdmin(Long id) {
        final User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        final Role role = roleRepository.findByName("ROLE_ADMIN").orElseThrow();

        if (!user.getRoles().contains(role)) {
            user.getRoles().add(role);

            userRepository.save(user);

            return userMapper.toFullProfileDTO(user);
        }
        throw new BadRequestException("Пользователь уже имеет роль админа");
    }
}
