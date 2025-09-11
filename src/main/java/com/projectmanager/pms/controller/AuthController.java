package com.projectmanager.pms.controller;

import com.projectmanager.pms.dto.AuthResponseDto;
import com.projectmanager.pms.dto.UserLoginDto;
import com.projectmanager.pms.dto.UserRegistrationDto;
import com.projectmanager.pms.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for authentication operations
 * Handles user registration, login, and password management
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")  // Allow React app to call these APIs
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * User registration endpoint
     * URL: POST /api/auth/register
     * @param registrationDto - user registration data
     * @return authentication response with JWT token
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> registerUser(@Valid @RequestBody UserRegistrationDto registrationDto) {
        AuthResponseDto response = userService.registerUser(registrationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * User login endpoint
     * URL: POST /api/auth/login
     * @param loginDto - login credentials
     * @return authentication response with JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> loginUser(@Valid @RequestBody UserLoginDto loginDto) {
        AuthResponseDto response = userService.loginUser(loginDto);
        return ResponseEntity.ok(response);
    }

    /**
     * Test endpoint to verify JWT token
     * URL: GET /api/auth/verify-token
     * @param authHeader - Authorization header with Bearer token
     * @return token verification result
     */
    @GetMapping("/verify-token")
    public ResponseEntity<?> verifyToken(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }

        try {
            String token = authHeader.substring(7); // Remove "Bearer " prefix
            // We'll implement proper token verification later with JWT filter
            return ResponseEntity.ok("Token format is valid");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    /**
     * Health check for authentication service
     * URL: GET /api/auth/health
     * @return service health status
     */
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok().body(
                java.util.Map.of(
                        "service", "Authentication Service",
                        "status", "UP",
                        "timestamp", java.time.LocalDateTime.now()
                )
        );
    }
}
