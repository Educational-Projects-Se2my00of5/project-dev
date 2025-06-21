package com.example.backend.exception;

// 401 - Unauthorized
public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message) {
        super(message);
    }
}