package com.projectmanager.pms.controller;

import com.projectmanager.pms.entity.Project;
import com.projectmanager.pms.entity.Task;
import com.projectmanager.pms.entity.User;
import com.projectmanager.pms.enums.Role;
import com.projectmanager.pms.enums.TaskPriority;
import com.projectmanager.pms.enums.TaskStatus;
import com.projectmanager.pms.repository.ProjectRepository;
import com.projectmanager.pms.repository.TaskRepository;
import com.projectmanager.pms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller to test database operations and entity relationships
 * This is for testing purposes only - we'll remove it later
 */
@RestController
@RequestMapping("/data-test")
@CrossOrigin(origins = "http://localhost:3000")
public class DataTestController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    /**
     * Create sample data for testing
     * URL: POST /api/data-test/create-sample-data
     */
    @PostMapping("/create-sample-data")
    public ResponseEntity<Map<String, Object>> createSampleData() {
        Map<String, Object> response = new HashMap<>();

        try {
            // Create sample users
            User admin = new User("John", "Doe", "john.doe@example.com", "password123");
            admin.setRole(Role.ADMIN);
            admin = userRepository.save(admin);

            User user1 = new User("Jane", "Smith", "jane.smith@example.com", "password123");
            user1.setRole(Role.USER);
            user1 = userRepository.save(user1);

            User user2 = new User("Bob", "Johnson", "bob.johnson@example.com", "password123");
            user2.setRole(Role.USER);
            user2 = userRepository.save(user2);

            // Create sample project
            Project project = new Project("E-commerce Website", "Building a modern e-commerce platform", admin);
            project.setStartDate(LocalDate.now());
            project.setEndDate(LocalDate.now().plusMonths(3));
            project.addMember(user1);
            project.addMember(user2);
            project = projectRepository.save(project);

            // Create sample tasks
            Task task1 = new Task("Setup project structure", "Initialize Spring Boot project and database", project, admin);
            task1.setPriority(TaskPriority.HIGH);
            task1.setDeadline(LocalDate.now().plusDays(7));
            task1.setAssignedTo(user1);
            taskRepository.save(task1);

            Task task2 = new Task("Design user interface", "Create wireframes and mockups", project, admin);
            task2.setPriority(TaskPriority.MEDIUM);
            task2.setStatus(TaskStatus.IN_PROGRESS);
            task2.setDeadline(LocalDate.now().plusDays(14));
            task2.setAssignedTo(user2);
            taskRepository.save(task2);

            Task task3 = new Task("Implement authentication", "Setup JWT authentication system", project, admin);
            task3.setPriority(TaskPriority.URGENT);
            task3.setDeadline(LocalDate.now().plusDays(10));
            task3.setAssignedTo(user1);
            taskRepository.save(task3);

            response.put("message", "Sample data created successfully!");
            response.put("users", userRepository.count());
            response.put("projects", projectRepository.count());
            response.put("tasks", taskRepository.count());
            response.put("status", "success");

        } catch (Exception e) {
            response.put("message", "Error creating sample data: " + e.getMessage());
            response.put("status", "error");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Get all users
     * URL: GET /api/data-test/users
     */
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    /**
     * Get all projects
     * URL: GET /api/data-test/projects
     */
    @GetMapping("/projects")
    public ResponseEntity<List<Project>> getAllProjects() {
        List<Project> projects = projectRepository.findAll();
        return ResponseEntity.ok(projects);
    }

    /**
     * Get all tasks
     * URL: GET /api/data-test/tasks
     */
    @GetMapping("/tasks")
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get tasks by project
     * URL: GET /api/data-test/projects/{projectId}/tasks
     */
    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity<List<Task>> getTasksByProject(@PathVariable Long projectId) {
        List<Task> tasks = taskRepository.findByProjectId(projectId);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get database statistics
     * URL: GET /api/data-test/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDatabaseStats() {
        Map<String, Object> stats = new HashMap<>();

        // User stats
        stats.put("totalUsers", userRepository.count());
        stats.put("activeUsers", userRepository.countByIsActive(true));
        stats.put("adminUsers", userRepository.findByRole(Role.ADMIN).size());

        // Project stats
        stats.put("totalProjects", projectRepository.count());
        stats.put("activeProjects", projectRepository.findByIsActive(true).size());

        // Task stats
        stats.put("totalTasks", taskRepository.count());
        stats.put("todoTasks", taskRepository.countByProjectIdAndStatus(1L, TaskStatus.TODO));
        stats.put("inProgressTasks", taskRepository.countByProjectIdAndStatus(1L, TaskStatus.IN_PROGRESS));
        stats.put("doneTasks", taskRepository.countByProjectIdAndStatus(1L, TaskStatus.DONE));

        return ResponseEntity.ok(stats);
    }

    /**
     * Clear all data (for testing)
     * URL: DELETE /api/data-test/clear-all
     */
    @DeleteMapping("/clear-all")
    public ResponseEntity<Map<String, String>> clearAllData() {
        taskRepository.deleteAll();
        projectRepository.deleteAll();
        userRepository.deleteAll();

        Map<String, String> response = new HashMap<>();
        response.put("message", "All data cleared successfully");
        response.put("status", "success");

        return ResponseEntity.ok(response);
    }
}