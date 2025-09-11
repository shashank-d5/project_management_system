package com.projectmanager.pms.dto;

import com.projectmanager.pms.enums.Role;

/**
 * DTO for authentication response (login/registration success)
 * Contains user information and JWT token
 */
public class AuthResponseDto {

    private String token;
    private String tokenType = "Bearer";  // JWT token type
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
    private String message;

    // Constructors
    public AuthResponseDto() {}

    public AuthResponseDto(String token, Long userId, String email, String firstName, String lastName, Role role, String message) {
        this.token = token;
        this.userId = userId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.message = message;
    }

    // Static factory methods for easy creation
    public static AuthResponseDto success(String token, Long userId, String email, String firstName, String lastName, Role role) {
        return new AuthResponseDto(token, userId, email, firstName, lastName, role, "Authentication successful");
    }

    public static AuthResponseDto registrationSuccess(String token, Long userId, String email, String firstName, String lastName, Role role) {
        return new AuthResponseDto(token, userId, email, firstName, lastName, role, "Registration successful");
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // Utility methods
    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return "AuthResponseDto{" +
                "tokenType='" + tokenType + '\'' +
                ", userId=" + userId +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", role=" + role +
                ", message='" + message + '\'' +
                ", token='[PROTECTED]'" +
                '}';
    }
}