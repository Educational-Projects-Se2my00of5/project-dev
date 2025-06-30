package com.example.backend.dto;

import com.example.backend.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Value;

import java.util.Set;

public enum UserDTO {
    ;

    public enum Request {
        ;

        @Value
        public static class Register implements Username, Email, Password {

            @NotBlank
            String username;

            @NotBlank
            String email;

            @NotBlank
            String password;
        }

        @Value
        public static class Login implements Email, Password {

            @NotBlank
            String email;

            @NotBlank
            String password;
        }

        @Data
        public static class EditProfile implements Username {

            String username;
        }

        @Data
        public static class EditUser implements Username, Password {

            String username;

            String password;
        }

        @Value
        public static class EditPassword implements OldPassword, Password {
            String oldPassword;

            @NotBlank
            @Schema(description = "Новый пароль", example = "newpassword123")
            String password;
        }

        @Value
        public static class RefreshToken implements UserDTO.RefreshToken {
            @NotBlank
            String refreshToken;
        }

        @Value
        public static class GiveRole implements RoleName {
            String roleName;
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
        public static class FullProfile implements Id, Username, Email, Roles {

            Long id;

            String username;

            String email;

            Set<Role> roles;
        }

        @Value
        public static class ShortProfile implements Id, Username, Roles {

            Long id;

            String username;

            Set<Role> roles;
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
        @Schema(description = "Имя пользователя", example = "john_doe")
        String getUsername();
    }

    private interface OldPassword {
        @Size(min = 6, max = 50, message = "Пароль должен быть от 6 до 50 символов")
        @Schema(description = "Старый пароль", example = "newpassword123")
        String getPassword();
    }

    private interface Password {
        @Size(min = 6, max = 50, message = "Пароль должен быть от 6 до 50 символов")
        @Schema(description = "Пароль", example = "newpassword123")
        String getPassword();
    }

    private interface Email {
        @jakarta.validation.constraints.Email
        @Schema(description = "Email", example = "john@example.com")
        String getEmail();
    }

    private interface Roles {
        @Schema(description = "Список ролей пользователя", example = "[\"ROLE_USER\"]")
        Set<Role> getRoles();
    }

    private interface RoleName {
        @Schema(description = "Название роли", example = "ROLE_USER")
        String getRoleName();
    }

    private interface AccessToken {
        @Schema(description = "Access токен", example = "eyJhbGciOiJIUzI1NiIsInR...")
        String getAccessToken();
    }

    private interface RefreshToken {
        @Schema(description = "Refresh токен", example = "eyJhbGciOiJIUzI1NiIsInR...")
        String getRefreshToken();
    }
}