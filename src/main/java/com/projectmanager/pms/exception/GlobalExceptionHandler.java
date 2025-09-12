package com.projectmanager.pms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the entire application
 * Catches exceptions and returns consistent error responses
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle validation errors (from @Valid annotations)
     * @param ex - method argument validation exception
     * @return validation error response
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();

        // Extract field validation errors
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        response.put("status", "error");
        response.put("message", "Validation failed");
        response.put("errors", errors);
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle user not found exceptions
     * @param ex - user not found exception
     * @return error response
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFoundException(UserNotFoundException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", ex.getMessage());
        response.put("errorCode", "USER_NOT_FOUND");
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Handle email already exists exceptions
     * @param ex - email already exists exception
     * @return error response
     */
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", ex.getMessage());
        response.put("errorCode", "EMAIL_ALREADY_EXISTS");
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /**
     * Handle invalid credentials exceptions
     * @param ex - invalid credentials exception
     * @return error response
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", ex.getMessage());
        response.put("errorCode", "INVALID_CREDENTIALS");
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * Handle project not found exceptions
     * @param ex - project not found exception
     * @return error response
     */
    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleProjectNotFoundException(ProjectNotFoundException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", ex.getMessage());
        response.put("errorCode", "PROJECT_NOT_FOUND");
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Handle project access denied exceptions
     * @param ex - project access denied exception
     * @return error response
     */
    @ExceptionHandler(ProjectAccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleProjectAccessDeniedException(ProjectAccessDeniedException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", ex.getMessage());
        response.put("errorCode", "PROJECT_ACCESS_DENIED");
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    /**
     * Handle illegal argument exceptions
     * @param ex - illegal argument exception
     * @return error response
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", ex.getMessage());
        response.put("errorCode", "INVALID_ARGUMENT");
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle JWT related exceptions
     * @param ex - runtime exception (including JWT exceptions)
     * @return error response
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleJwtException(RuntimeException ex) {
        // Check if it's a JWT-related error
        if (ex.getMessage() != null && ex.getMessage().contains("JWT")) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Invalid or expired token");
            response.put("errorCode", "INVALID_TOKEN");
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // For other runtime exceptions, return generic error
        return handleGenericException(ex);
    }

    /**
     * Handle generic exceptions (catch-all)
     * @param ex - any unhandled exception
     * @return generic error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "An unexpected error occurred");
        response.put("errorCode", "INTERNAL_SERVER_ERROR");
        response.put("timestamp", LocalDateTime.now());

        // In development, include the actual error message
        // In production, you might want to remove this for security
        response.put("details", ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}