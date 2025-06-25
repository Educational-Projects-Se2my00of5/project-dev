package com.example.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;


public enum MessageDTO {;
    public enum Response {
        ;

        @Value
        public static class GetMessage implements Message {
            String message;
        }

    }

    private interface Message {
        @Schema(description = "Сообщение", example = "Операция выполнена успешно")
        String getMessage();
    }
}
