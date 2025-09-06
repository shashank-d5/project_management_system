package com.projectmanager.pms.repository;

import com.projectmanager.pms.entity.Project;
import com.projectmanager.pms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for Project entity
 * Provides database operations for projects
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    /**
     * Find all active projects
     * @param isActive - active status
     * @return List of active projects
     */
    List<Project> findByIsActive(Boolean isActive);

    /**
     * Find all projects owned by a specific user
     * @param owner - project owner
     * @return List of projects owned by the user
     */
    List<Project> findByOwner(User owner);

    /**
     * Find all projects owned by a specific user ID
     * @param ownerId - ID of project owner
     * @return List of projects owned by the user
     */
    List<Project> findByOwnerId(Long ownerId);

    /**
     * Find projects where user is a member (including owned projects)
     * @param userId - ID of the user
     * @return List of projects where user is a member
     */
    @Query("SELECT DISTINCT p FROM Project p JOIN p.members m WHERE m.id = :userId AND p.isActive = true")
    List<Project> findProjectsByMemberId(@Param("userId") Long userId);

    /**
     * Find projects by name (case insensitive search)
     * @param name - project name to search
     * @return List of projects matching the name
     */
    List<Project> findByNameContainingIgnoreCase(String name);

    /**
     * Find projects with deadline approaching (within specified days)
     * @param date - date to compare against
     * @return List of projects with approaching deadline
     */
    @Query("SELECT p FROM Project p WHERE p.endDate IS NOT NULL AND p.endDate <= :date AND p.isActive = true")
    List<Project> findProjectsWithDeadlineBefore(@Param("date") LocalDate date);

    /**
     * Count projects by owner
     * @param ownerId - ID of project owner
     * @return number of projects owned by the user
     */
    long countByOwnerId(Long ownerId);

    /**
     * Find projects where specific user is a member
     * @param user - user to search for
     * @return List of projects where user is a member
     */
    @Query("SELECT p FROM Project p JOIN p.members m WHERE m = :user AND p.isActive = true")
    List<Project> findByMembersContaining(@Param("user") User user);

    /**
     * Get project statistics - total tasks, completed tasks
     * @param projectId - ID of the project
     * @return Object array with [totalTasks, completedTasks]
     */
    @Query("SELECT COUNT(t), SUM(CASE WHEN t.status = 'DONE' THEN 1 ELSE 0 END) " +
            "FROM Task t WHERE t.project.id = :projectId")
    Object[] getProjectTaskStats(@Param("projectId") Long projectId);

    /**
     * Find projects that are overdue (end date passed but still active)
     * @return List of overdue projects
     */
    @Query("SELECT p FROM Project p WHERE p.endDate < CURRENT_DATE AND p.isActive = true")
    List<Project> findOverdueProjects();

    /**
     * Find recently created projects (within last N days)
     * @param daysAgo - number of days ago
     * @return List of recently created projects
     */
    @Query("SELECT p FROM Project p WHERE p.createdAt >= :daysAgo AND p.isActive = true")
    List<Project> findRecentProjects(@Param("daysAgo") LocalDate daysAgo);
}