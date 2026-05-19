package com.stud.backend.common.exception;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return new ApiError(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                "NOT_FOUND",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(DuplicateResourceException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDuplicate(DuplicateResourceException ex, HttpServletRequest request) {
        return new ApiError(
                Instant.now(),
                HttpStatus.CONFLICT.value(),
                "DUPLICATE_RESOURCE",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return new ApiError(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "VALIDATION_ERROR",
                message,
                request.getRequestURI()
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiError handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        return new ApiError(
                Instant.now(),
                HttpStatus.UNAUTHORIZED.value(),
                "BAD_CREDENTIALS",
                "Invalid email or password",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(DisabledException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleDisabled(DisabledException ex, HttpServletRequest request) {
        return new ApiError(
                Instant.now(),
                HttpStatus.FORBIDDEN.value(),
                "USER_DISABLED",
                ex.getMessage(),
                request.getRequestURI()
        );
    }
}