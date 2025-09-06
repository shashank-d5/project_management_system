package com.projectmanager.pms.repository;

import com.projectmanager.pms.entity.User;
import com.projectmanager.pms.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity
 * JpaRepository provides basic CRUD operations automatically
 * We can add custom query methods here
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by email address (used for login)
     * Spring Data JPA automatically implements this method based on method name
     * @param email - email address to search for
     * @return Optional containing user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Find user by email and active status
     * @param email - email address
     * @param isActive - active status
     * @return Optional containing user if found
     */
    Optional<User> findByEmailAndIsActive(String email, Boolean isActive);

    /**
     * Check if email already exists (for registration validation)
     * @param email - email to check
     * @return true if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Find all users with specific role
     * @param role - user role to filter by
     * @return List of users with the specified role
     */
    List<User> findByRole(Role role);

    /**
     * Find all active users
     * @return List of active users
     */
    List<User> findByIsActive(Boolean isActive);

    /**
     * Find users by first name or last name (case insensitive search)
     * Custom JPQL query for name-based search
     * @param searchTerm - search term to match against names
     * @return List of matching users
     */
    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<User> findByNameContainingIgnoreCase(@Param("searchTerm") String searchTerm);

    /**
     * Find all users who are members of a specific project
     * @param projectId - ID of the project
     * @return List of users who are members of the project
     */
    @Query("SELECT u FROM User u JOIN u.projects p WHERE p.id = :projectId")
    List<User> findByProjectId(@Param("projectId") Long projectId);

    /**
     * Count total number of active users
     * @return number of active users
     */
    long countByIsActive(Boolean isActive);

    /**
     * Find users who are not members of a specific project (for invitation)
     * @param projectId - ID of the project
     * @return List of users not in the project
     */
    @Query("SELECT u FROM User u WHERE u.id NOT IN " +
            "(SELECT m.id FROM Project p JOIN p.members m WHERE p.id = :projectId)")
    List<User> findUsersNotInProject(@Param("projectId") Long projectId);
}