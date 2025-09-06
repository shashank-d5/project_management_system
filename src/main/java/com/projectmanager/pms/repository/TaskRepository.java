package com.projectmanager.pms.repository;

import com.projectmanager.pms.entity.Task;
import com.projectmanager.pms.entity.User;
import com.projectmanager.pms.enums.TaskPriority;
import com.projectmanager.pms.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for Task entity
 * Provides database operations for tasks with optimized queries
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Find all tasks in a specific project
     * @param projectId - ID of the project
     * @return List of tasks in the project
     */
    List<Task> findByProjectId(Long projectId);

    /**
     * Find tasks by status within a project (for Kanban board)
     * @param projectId - ID of the project
     * @param status - task status
     * @return List of tasks with specified status
     */
    List<Task> findByProjectIdAndStatus(Long projectId, TaskStatus status);

    /**
     * Find all tasks assigned to a specific user
     * @param assignedTo - assigned user
     * @return List of tasks assigned to the user
     */
    List<Task> findByAssignedTo(User assignedTo);

    /**
     * Find tasks assigned to a user by user ID
     * @param assignedToId - ID of assigned user
     * @return List of tasks assigned to the user
     */
    List<Task> findByAssignedToId(Long assignedToId);

    /**
     * Find tasks by priority within a project
     * @param projectId - ID of the project
     * @param priority - task priority
     * @return List of tasks with specified priority
     */
    List<Task> findByProjectIdAndPriority(Long projectId, TaskPriority priority);

    /**
     * Find overdue tasks (deadline passed, not completed)
     * @param currentDate - current date
     * @return List of overdue tasks
     */
    @Query("SELECT t FROM Task t WHERE t.deadline < :currentDate AND t.status != 'DONE'")
    List<Task> findOverdueTasks(@Param("currentDate") LocalDate currentDate);

    /**
     * Find tasks due within specified days
     * @param fromDate - start date
     * @param toDate - end date
     * @return List of tasks due within the date range
     */
    @Query("SELECT t FROM Task t WHERE t.deadline BETWEEN :fromDate AND :toDate AND t.status != 'DONE'")
    List<Task> findTasksDueBetween(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

    /**
     * Find tasks by title (case insensitive search)
     * @param projectId - ID of the project
     * @param searchTerm - search term
     * @return List of tasks matching the search term
     */
    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId AND " +
            "LOWER(t.title) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Task> findByProjectIdAndTitleContainingIgnoreCase(
            @Param("projectId") Long projectId,
            @Param("searchTerm") String searchTerm
    );

    /**
     * Count tasks by status in a project
     * @param projectId - ID of the project
     * @param status - task status
     * @return number of tasks with the specified status
     */
    long countByProjectIdAndStatus(Long projectId, TaskStatus status);

    /**
     * Find unassigned tasks in a project
     * @param projectId - ID of the project
     * @return List of unassigned tasks
     */
    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId AND t.assignedTo IS NULL")
    List<Task> findUnassignedTasksByProject(@Param("projectId") Long projectId);

    /**
     * Find tasks created by a specific user
     * @param createdById - ID of task creator
     * @return List of tasks created by the user
     */
    List<Task> findByCreatedById(Long createdById);

    /**
     * Get task statistics for a project
     * @param projectId - ID of the project
     * @return Object array with [totalTasks, todoTasks, inProgressTasks, doneTasks]
     */
    @Query("SELECT " +
            "COUNT(t), " +
            "SUM(CASE WHEN t.status = 'TODO' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN t.status = 'IN_PROGRESS' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN t.status = 'DONE' THEN 1 ELSE 0 END) " +
            "FROM Task t WHERE t.project.id = :projectId")
    Object[] getTaskStatsByProject(@Param("projectId") Long projectId);

    /**
     * Find high priority tasks that are not completed
     * @param projectId - ID of the project
     * @return List of high priority pending tasks
     */
    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId AND " +
            "t.priority IN ('HIGH', 'URGENT') AND t.status != 'DONE' " +
            "ORDER BY t.priority DESC, t.deadline ASC")
    List<Task> findHighPriorityPendingTasks(@Param("projectId") Long projectId);

    /**
     * Find tasks assigned to user in specific projects
     * @param userId - ID of the user
     * @param projectIds - List of project IDs
     * @return List of tasks assigned to user in the projects
     */
    @Query("SELECT t FROM Task t WHERE t.assignedTo.id = :userId AND t.project.id IN :projectIds")
    List<Task> findByAssignedToIdAndProjectIdIn(@Param("userId") Long userId, @Param("projectIds") List<Long> projectIds);

    /**
     * Find recently updated tasks (within last N days)
     * @param projectId - ID of the project
     * @param daysAgo - date N days ago
     * @return List of recently updated tasks
     */
    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId AND t.updatedAt >= :daysAgo " +
            "ORDER BY t.updatedAt DESC")
    List<Task> findRecentlyUpdatedTasks(@Param("projectId") Long projectId, @Param("daysAgo") LocalDate daysAgo);
}