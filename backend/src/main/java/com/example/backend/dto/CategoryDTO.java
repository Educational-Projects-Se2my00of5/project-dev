package com.example.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Value;

public enum CategoryDTO {
    ;

    public enum Request {
        ;


        @Value
        public static class CreateOrUpdateCategory implements Name, Description {
            @NotBlank
            String name;

            String description;
        }
    }

    public enum Response {
        ;


        @Value
        public static class FullInfoCategory implements Id, Name, Description {
            Long id;
            String name;
            String description;
        }

        @Value
        public static class ShortInfoCategory implements Id, Name {
            Long id;
            String name;
        }
    }

    private interface Id {
        @Positive
        @Schema(description = "ID категории", example = "1")
        Long getId();
    }

    private interface Name {
        @Schema(description = "Название категории", example = "Java")
        String getName();
    }

    private interface Description {
        @Schema(description = "Описание категории", example = "Все о языке программирования Java")
        String getDescription();
    }
}