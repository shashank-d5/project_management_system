package com.projectmanager.pms.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Test Controller to verify our Spring Boot setup is working
 * This will be our first REST API endpoint
 */
@RestController  // Combines @Controller + @ResponseBody - returns JSON by default
@RequestMapping("/test")  // Base URL: /api/test (remember we set context-path to /api)
@CrossOrigin(origins = "http://localhost:3000")  // Allow React app to call our APIs
public class TestController {

    /**
     * Simple GET endpoint to test if our server is running
     * URL: GET /api/test/hello
     */
    @GetMapping("/hello")
    public ResponseEntity<Map<String, Object>> sayHello() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Hello from Spring Boot!");
        response.put("timestamp", LocalDateTime.now());
        response.put("status", "success");

        // ResponseEntity allows us to control HTTP status codes and headers
        return ResponseEntity.ok(response);  // Returns 200 OK with JSON body
    }

    /**
     * POST endpoint to test sending data
     * URL: POST /api/test/echo
     */
    @PostMapping("/echo")
    public ResponseEntity<Map<String, Object>> echoMessage(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        // Extract message from request body
        String message = request.getOrDefault("message", "No message provided");

        response.put("originalMessage", message);
        response.put("echo", "Echo: " + message);
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    /**
     * GET endpoint with path variable
     * URL: GET /api/test/user/123
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserInfo(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("message", "User info for ID: " + userId);
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint - useful for deployment
     * URL: GET /api/test/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "Project Management System");
        health.put("version", "1.0.0");

        return ResponseEntity.ok(health);
    }
}