package com.projectmanager.pms.entity;

import com.projectmanager.pms.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.HashSet;
import java.util.Set;

/**
 * User Entity - Represents users in our system
 * Extends BaseEntity to get id, createdAt, updatedAt fields
 */
@Entity
@Table(name = "users")  // Table name (avoiding 'user' as it's reserved in some databases)
public class User extends BaseEntity {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)  // Store enum as string in database
    @Column(name = "role", nullable = false)
    private Role role = Role.USER;  // Default role

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;  // For soft delete functionality

    // Many-to-Many relationship with Project
    @ManyToMany(mappedBy = "members", fetch = FetchType.LAZY)
    private Set<Project> projects = new HashSet<>();

    // One-to-Many relationship with Task (tasks assigned to this user)
    @OneToMany(mappedBy = "assignedTo", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Task> assignedTasks = new HashSet<>();

    // Constructors
    public User() {}

    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    // Getters and Setters
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Set<Project> getProjects() {
        return projects;
    }

    public void setProjects(Set<Project> projects) {
        this.projects = projects;
    }

    public Set<Task> getAssignedTasks() {
        return assignedTasks;
    }

    public void setAssignedTasks(Set<Task> assignedTasks) {
        this.assignedTasks = assignedTasks;
    }

    // Utility Methods
    /**
     * Get full name of the user
     * @return firstName + lastName
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Check if user is an admin
     * @return true if user has ADMIN role
     */
    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    /**
     * Add project to user's projects
     * @param project - project to add
     */
    public void addProject(Project project) {
        projects.add(project);
        project.getMembers().add(this);
    }

    /**
     * Remove project from user's projects
     * @param project - project to remove
     */
    public void removeProject(Project project) {
        projects.remove(project);
        project.getMembers().remove(this);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", isActive=" + isActive +
                '}';
    }
}