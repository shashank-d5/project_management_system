package com.projectmanager.pms.controller;

import com.projectmanager.pms.dto.ProjectCreateDto;
import com.projectmanager.pms.dto.ProjectMemberDto;
import com.projectmanager.pms.dto.ProjectResponseDto;
import com.projectmanager.pms.service.ProjectService;
import com.projectmanager.pms.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for project management operations
 * Handles project CRUD operations and member management
 */
@RestController
@RequestMapping("/projects")
@CrossOrigin(origins = "http://localhost:3000")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Create a new project
     * URL: POST /api/projects
     * @param createDto - project creation data
     * @param authHeader - Authorization header with JWT token
     * @return created project response
     */
    @PostMapping
    public ResponseEntity<ProjectResponseDto> createProject(
            @Valid @RequestBody ProjectCreateDto createDto,
            @RequestHeader("Authorization") String authHeader) {

        Long userId = extractUserIdFromToken(authHeader);
        ProjectResponseDto project = projectService.createProject(createDto, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(project);
    }

    /**
     * Get all projects for the authenticated user
     * URL: GET /api/projects
     * @param authHeader - Authorization header with JWT token
     * @return list of user's projects
     */
    @GetMapping
    public ResponseEntity<List<ProjectResponseDto>> getUserProjects(
            @RequestHeader("Authorization") String authHeader) {

        Long userId = extractUserIdFromToken(authHeader);
        List<ProjectResponseDto> projects = projectService.getUserProjects(userId);

        return ResponseEntity.ok(projects);
    }

    /**
     * Get projects owned by the authenticated user
     * URL: GET /api/projects/owned
     * @param authHeader - Authorization header with JWT token
     * @return list of owned projects
     */
    @GetMapping("/owned")
    public ResponseEntity<List<ProjectResponseDto>> getOwnedProjects(
            @RequestHeader("Authorization") String authHeader) {

        Long userId = extractUserIdFromToken(authHeader);
        List<ProjectResponseDto> projects = projectService.getOwnedProjects(userId);

        return ResponseEntity.ok(projects);
    }

    /**
     * Get project by ID
     * URL: GET /api/projects/{projectId}
     * @param projectId - project ID
     * @param authHeader - Authorization header with JWT token
     * @return project details
     */
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponseDto> getProjectById(
            @PathVariable Long projectId,
            @RequestHeader("Authorization") String authHeader) {

        Long userId = extractUserIdFromToken(authHeader);
        ProjectResponseDto project = projectService.getProjectById(projectId, userId);

        return ResponseEntity.ok(project);
    }

    /**
     * Update project
     * URL: PUT /api/projects/{projectId}
     * @param projectId - project ID
     * @param updateDto - updated project data
     * @param authHeader - Authorization header with JWT token
     * @return updated project response
     */
    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectResponseDto> updateProject(
            @PathVariable Long projectId,
            @Valid @RequestBody ProjectCreateDto updateDto,
            @RequestHeader("Authorization") String authHeader) {

        Long userId = extractUserIdFromToken(authHeader);
        ProjectResponseDto project = projectService.updateProject(projectId, updateDto, userId);

        return ResponseEntity.ok(project);
    }

    /**
     * Delete (deactivate) project
     * URL: DELETE /api/projects/{projectId}
     * @param projectId - project ID
     * @param authHeader - Authorization header with JWT token
     * @return success message
     */
    @DeleteMapping("/{projectId}")
    public ResponseEntity<Map<String, String>> deleteProject(
            @PathVariable Long projectId,
            @RequestHeader("Authorization") String authHeader) {

        Long userId = extractUserIdFromToken(authHeader);
        projectService.deleteProject(projectId, userId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Project deleted successfully");
        response.put("status", "success");

        return ResponseEntity.ok(response);
    }

    /**
     * Add member to project
     * URL: POST /api/projects/{projectId}/members
     * @param projectId - project ID
     * @param memberDto - member data
     * @param authHeader - Authorization header with JWT token
     * @return success message
     */
    @PostMapping("/{projectId}/members")
    public ResponseEntity<Map<String, String>> addMemberToProject(
            @PathVariable Long projectId,
            @Valid @RequestBody ProjectMemberDto memberDto,
            @RequestHeader("Authorization") String authHeader) {

        Long userId = extractUserIdFromToken(authHeader);
        String message = projectService.addMemberToProject(projectId, memberDto, userId);

        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        response.put("status", "success");

        return ResponseEntity.ok(response);
    }

    /**
     * Remove member from project
     * URL: DELETE /api/projects/{projectId}/members/{userIdToRemove}
     * @param projectId - project ID
     * @param userIdToRemove - ID of user to remove
     * @param authHeader - Authorization header with JWT token
     * @return success message
     */
    @DeleteMapping("/{projectId}/members/{userIdToRemove}")
    public ResponseEntity<Map<String, String>> removeMemberFromProject(
            @PathVariable Long projectId,
            @PathVariable Long userIdToRemove,
            @RequestHeader("Authorization") String authHeader) {

        Long userId = extractUserIdFromToken(authHeader);
        String message = projectService.removeMemberFromProject(projectId, userIdToRemove, userId);

        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        response.put("status", "success");

        return ResponseEntity.ok(response);
    }

    /**
     * Get project members
     * URL: GET /api/projects/{projectId}/members
     * @param projectId - project ID
     * @param authHeader - Authorization header with JWT token
     * @return list of project members
     */
    @GetMapping("/{projectId}/members")
    public ResponseEntity<List<Map<String, Object>>> getProjectMembers(
            @PathVariable Long projectId,
            @RequestHeader("Authorization") String authHeader) {

        Long userId = extractUserIdFromToken(authHeader);
        List<Map<String, Object>> members = projectService.getProjectMembers(projectId, userId);

        return ResponseEntity.ok(members);
    }

    /**
     * Search projects by name
     * URL: GET /api/projects/search?q=searchTerm
     * @param searchTerm - search query
     * @param authHeader - Authorization header with JWT token
     * @return list of matching projects
     */
    @GetMapping("/search")
    public ResponseEntity<List<ProjectResponseDto>> searchProjects(
            @RequestParam("q") String searchTerm,
            @RequestHeader("Authorization") String authHeader) {

        Long userId = extractUserIdFromToken(authHeader);
        List<ProjectResponseDto> projects = projectService.searchProjects(searchTerm, userId);

        return ResponseEntity.ok(projects);
    }

    /**
     * Get project statistics for dashboard
     * URL: GET /api/projects/stats
     * @param authHeader - Authorization header with JWT token
     * @return project statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getProjectStats(
            @RequestHeader("Authorization") String authHeader) {

        Long userId = extractUserIdFromToken(authHeader);
        List<ProjectResponseDto> userProjects = projectService.getUserProjects(userId);
        List<ProjectResponseDto> ownedProjects = projectService.getOwnedProjects(userId);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalProjects", userProjects.size());
        stats.put("ownedProjects", ownedProjects.size());
        stats.put("memberProjects", userProjects.size() - ownedProjects.size());

        // Calculate completion statistics
        double avgCompletion = userProjects.stream()
                .mapToDouble(ProjectResponseDto::getCompletionPercentage)
                .average()
                .orElse(0.0);
        stats.put("averageCompletion", Math.round(avgCompletion * 100.0) / 100.0);

        // Count overdue projects
        long overdueCount = userProjects.stream()
                .mapToLong(project -> Boolean.TRUE.equals(project.getIsOverdue()) ? 1 : 0)
                .sum();
        stats.put("overdueProjects", overdueCount);

        // Total tasks across all projects
        int totalTasks = userProjects.stream()
                .mapToInt(ProjectResponseDto::getTaskCount)
                .sum();
        stats.put("totalTasks", totalTasks);

        // Completed tasks across all projects
        long completedTasks = userProjects.stream()
                .mapToLong(ProjectResponseDto::getCompletedTasks)
                .sum();
        stats.put("completedTasks", completedTasks);

        stats.put("timestamp", java.time.LocalDateTime.now());

        return ResponseEntity.ok(stats);
    }

    /**
     * Get recent projects (last 30 days)
     * URL: GET /api/projects/recent
     * @param authHeader - Authorization header with JWT token
     * @return list of recent projects
     */
    @GetMapping("/recent")
    public ResponseEntity<List<ProjectResponseDto>> getRecentProjects(
            @RequestHeader("Authorization") String authHeader) {

        Long userId = extractUserIdFromToken(authHeader);
        List<ProjectResponseDto> projects = projectService.getUserProjects(userId);

        // Filter projects created in last 30 days
        java.time.LocalDateTime thirtyDaysAgo = java.time.LocalDateTime.now().minusDays(30);
        List<ProjectResponseDto> recentProjects = projects.stream()
                .filter(project -> project.getCreatedAt().isAfter(thirtyDaysAgo))
                .collect(java.util.stream.Collectors.toList());

        return ResponseEntity.ok(recentProjects);
    }

    /**
     * Health check for project service
     * URL: GET /api/projects/health
     * @return service health status
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("service", "Project Management Service");
        health.put("status", "UP");
        health.put("timestamp", java.time.LocalDateTime.now());

        return ResponseEntity.ok(health);
    }

    /**
     * Helper method to extract user ID from JWT token
     * @param authHeader - Authorization header with Bearer token
     * @return user ID from token
     */
    private Long extractUserIdFromToken(String authHeader) {
        String token = authHeader.substring(7); // Remove "Bearer " prefix
        return jwtUtil.extractUserId(token);
    }
}