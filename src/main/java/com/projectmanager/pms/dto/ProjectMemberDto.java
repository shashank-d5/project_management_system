package com.projectmanager.pms.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO for project member operations
 * Used for adding/removing members from projects
 */
public class ProjectMemberDto {

    @NotNull(message = "User ID is required")
    private Long userId;

    private String email; // Optional - can add member by email

    // Constructors
    public ProjectMemberDto() {}

    public ProjectMemberDto(Long userId) {
        this.userId = userId;
    }

    public ProjectMemberDto(String email) {
        this.email = email;
    }

    public ProjectMemberDto(Long userId, String email) {
        this.userId = userId;
        this.email = email;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "ProjectMemberDto{" +
                "userId=" + userId +
                ", email='" + email + '\'' +
                '}';
    }
}