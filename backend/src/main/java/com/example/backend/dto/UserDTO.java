package com.example.backend.dto;

import com.example.backend.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;


import java.util.Set;

public enum UserDTO {
    ;

    public enum Request {
        ;

        @Value
        public static class Register implements Username, Email, Password {

            String username;

            String email;

            String password;
        }

        @Value
        public static class Login implements Email, Password {

            String email;

            String password;
        }

        @Value
        public static class EditUsername implements Username {
            String username;
        }

        @Value
        public static class EditPassword implements OldPassword, Password {

            String oldPassword;

            @Schema(description = "Новый пароль", example = "newpassword123")
            String password;
        }

        @Value
        public static class RefreshToken implements UserDTO.RefreshToken {
            String refreshToken;
        }
    }

    public enum Response {
        ;

        @Value
        public static class TokenAndShortUserInfo implements RefreshToken, Id, Email {

            String refreshToken;

            Long id;

            String email;
        }

        @Value
        public static class Profile implements Id, Username, Email, Password, Roles {

            Long id;

            String username;

            String email;

            String password;

            Set<Role> roles;
        }

        @Value
        public static class GetMessage implements Message {
            String message;
        }

        @Value
        public static class PairTokens implements AccessToken, RefreshToken {
            String accessToken;
            String refreshToken;
        }
    }

    private interface Id {
        @Positive
        @Schema(description = "ID пользователя", example = "1")
        Long getId();
    }

    private interface Username {
        @NotBlank
        @Schema(description = "Имя пользователя", example = "john_doe")
        String getUsername();
    }

    private interface OldPassword {
        @NotBlank
        @Size(min = 6, max = 50, message = "Пароль должен быть от 6 до 50 символов")
        @Schema(description = "Старый пароль", example = "newpassword123")
        String getPassword();
    }

    private interface Password {
        @NotBlank
        @Size(min = 6, max = 50, message = "Пароль должен быть от 6 до 50 символов")
        @Schema(description = "Пароль", example = "newpassword123")
        String getPassword();
    }

    private interface Email {
        @NotBlank
        @jakarta.validation.constraints.Email
        @Schema(description = "Email", example = "john@example.com")
        String getEmail();
    }

    private interface Roles {
        @NotNull
        @Schema(description = "Список ролей пользователя", example = "[\"ROLE_USER\"]")
        Set<Role> getRoles();
    }

    private interface AccessToken {
        @NotBlank
        @Schema(description = "Access токен", example = "eyJhbGciOiJIUzI1NiIsInR...")
        String getAccessToken();
    }

    private interface RefreshToken {
        @NotBlank
        @Schema(description = "Refresh токен", example = "eyJhbGciOiJIUzI1NiIsInR...")
        String getRefreshToken();
    }

    private interface Message {
        @NotBlank
        @Schema(description = "Сообщение", example = "Операция выполнена успешно")
        String getMessage();
    }
}