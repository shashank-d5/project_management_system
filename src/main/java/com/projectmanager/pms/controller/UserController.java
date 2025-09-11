package com.projectmanager.pms.controller;

import com.projectmanager.pms.dto.UserRegistrationDto;
import com.projectmanager.pms.entity.User;
import com.projectmanager.pms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for user management operations
 * Handles user profile, search, and administrative functions
 */
@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Get current user profile
     * URL: GET /api/users/profile/{userId}
     * @param userId - user ID
     * @return user profile information
     */
    @GetMapping("/profile/{userId}")
    public ResponseEntity<Map<String, Object>> getUserProfile(@PathVariable Long userId) {
        User user = userService.getUserById(userId);

        Map<String, Object> profile = new HashMap<>();
        profile.put("id", user.getId());
        profile.put("firstName", user.getFirstName());
        profile.put("lastName", user.getLastName());
        profile.put("fullName", user.getFullName());
        profile.put("email", user.getEmail());
        profile.put("role", user.getRole());
        profile.put("isActive", user.getIsActive());
        profile.put("createdAt", user.getCreatedAt());
        profile.put("projectCount", user.getProjects().size());
        profile.put("taskCount", user.getAssignedTasks().size());

        return ResponseEntity.ok(profile);
    }

    /**
     * Get all active users
     * URL: GET /api/users
     * @return list of all active users
     */
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        List<User> users = userService.getAllActiveUsers();

        List<Map<String, Object>> userList = users.stream()
                .map(this::mapUserToResponse)
                .toList();

        return ResponseEntity.ok(userList);
    }

    /**
     * Search users by name
     * URL: GET /api/users/search?q=searchTerm
     * @param searchTerm - search query
     * @return list of matching users
     */
    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> searchUsers(@RequestParam("q") String searchTerm) {
        List<User> users = userService.searchUsersByName(searchTerm);

        List<Map<String, Object>> userList = users.stream()
                .map(this::mapUserToResponse)
                .toList();

        return ResponseEntity.ok(userList);
    }

    /**
     * Update user profile
     * URL: PUT /api/users/profile/{userId}
     * @param userId - user ID
     * @param updateDto - updated user data
     * @return updated user profile
     */
    @PutMapping("/profile/{userId}")
    public ResponseEntity<Map<String, Object>> updateUserProfile(
            @PathVariable Long userId,
            @RequestBody UserRegistrationDto updateDto) {

        User updatedUser = userService.updateUserProfile(userId, updateDto);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Profile updated successfully");
        response.put("user", mapUserToResponse(updatedUser));

        return ResponseEntity.ok(response);
    }

    /**
     * Change password
     * URL: PUT /api/users/change-password/{userId}
     * @param userId - user ID
     * @param passwordData - password change data
     * @return success message
     */
    @PutMapping("/change-password/{userId}")
    public ResponseEntity<Map<String, String>> changePassword(
            @PathVariable Long userId,
            @RequestBody Map<String, String> passwordData) {

        String currentPassword = passwordData.get("currentPassword");
        String newPassword = passwordData.get("newPassword");

        userService.changePassword(userId, currentPassword, newPassword);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Password changed successfully");
        response.put("status", "success");

        return ResponseEntity.ok(response);
    }

    /**
     * Deactivate user account
     * URL: DELETE /api/users/{userId}
     * @param userId - user ID to deactivate
     * @return success message
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Map<String, String>> deactivateUser(@PathVariable Long userId) {
        userService.deactivateUser(userId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "User account deactivated successfully");
        response.put("status", "success");

        return ResponseEntity.ok(response);
    }

    /**
     * Get user statistics
     * URL: GET /api/users/stats
     * @return user statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getUserStats() {
        List<User> allUsers = userService.getAllActiveUsers();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalActiveUsers", allUsers.size());
        stats.put("adminUsers", allUsers.stream()
                .mapToInt(user -> user.isAdmin() ? 1 : 0)
                .sum());
        stats.put("regularUsers", allUsers.stream()
                .mapToInt(user -> !user.isAdmin() ? 1 : 0)
                .sum());
        stats.put("timestamp", java.time.LocalDateTime.now());

        return ResponseEntity.ok(stats);
    }

    /**
     * Check if email is available for registration
     * URL: GET /api/users/check-email?email=test@example.com
     * @param email - email to check
     * @return availability status
     */
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Object>> checkEmailAvailability(@RequestParam String email) {
        boolean isAvailable = !userService.getAllActiveUsers().stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email.trim()));

        Map<String, Object> response = new HashMap<>();
        response.put("email", email);
        response.put("available", isAvailable);
        response.put("message", isAvailable ? "Email is available" : "Email is already taken");

        return ResponseEntity.ok(response);
    }

    /**
     * Helper method to map User entity to response format
     * @param user - user entity
     * @return mapped user data
     */
    private Map<String, Object> mapUserToResponse(User user) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", user.getId());
        userMap.put("firstName", user.getFirstName());
        userMap.put("lastName", user.getLastName());
        userMap.put("fullName", user.getFullName());
        userMap.put("email", user.getEmail());
        userMap.put("role", user.getRole());
        userMap.put("isActive", user.getIsActive());
        userMap.put("createdAt", user.getCreatedAt());
        return userMap;
    }
}