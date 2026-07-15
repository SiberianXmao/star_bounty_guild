package com.stud.dictionary.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import com.stud.dictionary.integrations.files.FileStorageException;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequest(BadRequestException ex, HttpServletRequest request) {
        logClientError(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage(), request);

        return new ApiError(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "BAD_REQUEST",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public ApiError handleUploadTooLarge(MaxUploadSizeExceededException ex, HttpServletRequest request) {
        logClientError(HttpStatus.PAYLOAD_TOO_LARGE, "FILE_TOO_LARGE", "Catalog image must not exceed 5 MB", request);

        return new ApiError(
                Instant.now(),
                HttpStatus.PAYLOAD_TOO_LARGE.value(),
                "FILE_TOO_LARGE",
                "Catalog image must not exceed 5 MB",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(FileStorageException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ApiError handleStorage(FileStorageException ex, HttpServletRequest request) {
        log.error(
                "Dictionary request failed because file storage is unavailable method={} path={} message='{}'",
                request.getMethod(),
                request.getRequestURI(),
                ex.getMessage(),
                ex
        );

        return new ApiError(
                Instant.now(),
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                "STORAGE_UNAVAILABLE",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        logClientError(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage(), request);

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
        logClientError(HttpStatus.CONFLICT, "DUPLICATE_RESOURCE", ex.getMessage(), request);

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

        logClientError(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", message, request);

        return new ApiError(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "VALIDATION_ERROR",
                message,
                request.getRequestURI()
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleUnexpected(Exception ex, HttpServletRequest request) {
        log.error(
                "Unhandled dictionary service exception method={} path={} message='{}'",
                request.getMethod(),
                request.getRequestURI(),
                ex.getMessage(),
                ex
        );

        return new ApiError(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                "Unexpected dictionary service error",
                request.getRequestURI()
        );
    }

    private void logClientError(HttpStatus status, String code, String message, HttpServletRequest request) {
        log.warn(
                "Dictionary request rejected status={} code={} method={} path={} message='{}'",
                status.value(),
                code,
                request.getMethod(),
                request.getRequestURI(),
                message
        );
    }
}
