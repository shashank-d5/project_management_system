package com.projectmanager.pms.service;

import com.projectmanager.pms.dto.ProjectCreateDto;
import com.projectmanager.pms.dto.ProjectMemberDto;
import com.projectmanager.pms.dto.ProjectResponseDto;
import com.projectmanager.pms.entity.Project;
import com.projectmanager.pms.entity.User;
import com.projectmanager.pms.exception.ProjectAccessDeniedException;
import com.projectmanager.pms.exception.ProjectNotFoundException;
import com.projectmanager.pms.exception.UserNotFoundException;
import com.projectmanager.pms.repository.ProjectRepository;
import com.projectmanager.pms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service class for project-related business logic
 * Handles project CRUD operations and member management
 */
@Service
@Transactional
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Create a new project
     * @param createDto - project creation data
     * @param ownerId - ID of the project owner (from JWT token)
     * @return created project response
     */
    public ProjectResponseDto createProject(ProjectCreateDto createDto, Long ownerId) {
        // Validate input
        validateProjectDto(createDto);

        // Get owner user
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new UserNotFoundException(ownerId));

        // Create new project
        Project project = new Project();
        project.setName(createDto.getName().trim());
        project.setDescription(createDto.getDescription() != null ? createDto.getDescription().trim() : null);
        project.setStartDate(createDto.getStartDate());
        project.setEndDate(createDto.getEndDate());
        project.setOwner(owner);
        project.setIsActive(true);

        // Owner is automatically a member
        project.addMember(owner);

        // Save project
        project = projectRepository.save(project);

        return convertToResponseDto(project);
    }

    /**
     * Get project by ID with authorization check
     * @param projectId - project ID
     * @param userId - requesting user ID (from JWT token)
     * @return project response
     */
    @Transactional(readOnly = true)
    public ProjectResponseDto getProjectById(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        // Check if user is a member of the project
        if (!isUserMemberOfProject(project, userId)) {
            throw new ProjectAccessDeniedException();
        }

        return convertToResponseDto(project);
    }

    /**
     * Get all projects for a user (projects where user is a member)
     * @param userId - user ID (from JWT token)
     * @return list of user's projects
     */
    @Transactional(readOnly = true)
    public List<ProjectResponseDto> getUserProjects(Long userId) {
        List<Project> projects = projectRepository.findProjectsByMemberId(userId);

        return projects.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Get projects owned by a user
     * @param userId - user ID
     * @return list of owned projects
     */
    @Transactional(readOnly = true)
    public List<ProjectResponseDto> getOwnedProjects(Long userId) {
        List<Project> projects = projectRepository.findByOwnerId(userId);

        return projects.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Update project information
     * @param projectId - project ID
     * @param updateDto - updated project data
     * @param userId - requesting user ID (must be owner)
     * @return updated project response
     */
    public ProjectResponseDto updateProject(Long projectId, ProjectCreateDto updateDto, Long userId) {
        // Validate input
        validateProjectDto(updateDto);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));

        // Check if user is the owner
        if (!project.isOwner(userRepository.findById(userId).orElseThrow())) {
            throw new RuntimeException("Access denied. Only project owner can update project.");
        }

        // Update project fields
        project.setName(updateDto.getName().trim());
        project.setDescription(updateDto.getDescription() != null ? updateDto.getDescription().trim() : null);
        project.setStartDate(updateDto.getStartDate());
        project.setEndDate(updateDto.getEndDate());

        project = projectRepository.save(project);

        return convertToResponseDto(project);
    }

    /**
     * Delete (deactivate) a project
     * @param projectId - project ID
     * @param userId - requesting user ID (must be owner)
     */
    public void deleteProject(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));

        // Check if user is the owner
        if (!project.isOwner(userRepository.findById(userId).orElseThrow())) {
            throw new RuntimeException("Access denied. Only project owner can delete project.");
        }

        // Soft delete - set as inactive
        project.setIsActive(false);
        projectRepository.save(project);
    }

    /**
     * Add member to project
     * @param projectId - project ID
     * @param memberDto - member data
     * @param userId - requesting user ID (must be owner or admin)
     * @return success message
     */
    public String addMemberToProject(Long projectId, ProjectMemberDto memberDto, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));

        // Check if user is the owner
        User requestingUser = userRepository.findById(userId).orElseThrow();
        if (!project.isOwner(requestingUser) && !requestingUser.isAdmin()) {
            throw new RuntimeException("Access denied. Only project owner can add members.");
        }

        // Find user to add
        User userToAdd;
        if (memberDto.getUserId() != null) {
            userToAdd = userRepository.findById(memberDto.getUserId())
                    .orElseThrow(() -> new UserNotFoundException(memberDto.getUserId()));
        } else if (memberDto.getEmail() != null) {
            userToAdd = userRepository.findByEmailAndIsActive(memberDto.getEmail(), true)
                    .orElseThrow(() -> new UserNotFoundException("email", memberDto.getEmail()));
        } else {
            throw new IllegalArgumentException("Either userId or email must be provided");
        }

        // Check if user is already a member
        if (project.isMember(userToAdd)) {
            throw new RuntimeException("User is already a member of this project");
        }

        // Add user to project
        project.addMember(userToAdd);
        projectRepository.save(project);

        return "User " + userToAdd.getFullName() + " added to project successfully";
    }

    /**
     * Remove member from project
     * @param projectId - project ID
     * @param userIdToRemove - ID of user to remove
     * @param userId - requesting user ID (must be owner)
     * @return success message
     */
    public String removeMemberFromProject(Long projectId, Long userIdToRemove, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));

        // Check if user is the owner
        User requestingUser = userRepository.findById(userId).orElseThrow();
        if (!project.isOwner(requestingUser)) {
            throw new RuntimeException("Access denied. Only project owner can remove members.");
        }

        // Find user to remove
        User userToRemove = userRepository.findById(userIdToRemove)
                .orElseThrow(() -> new UserNotFoundException(userIdToRemove));

        // Cannot remove the owner
        if (project.isOwner(userToRemove)) {
            throw new RuntimeException("Cannot remove project owner from the project");
        }

        // Check if user is a member
        if (!project.isMember(userToRemove)) {
            throw new RuntimeException("User is not a member of this project");
        }

        // Remove user from project
        project.removeMember(userToRemove);
        projectRepository.save(project);

        return "User " + userToRemove.getFullName() + " removed from project successfully";
    }

    /**
     * Get project members
     * @param projectId - project ID
     * @param userId - requesting user ID (must be member)
     * @return list of project members
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getProjectMembers(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));

        // Check if user is a member
        if (!isUserMemberOfProject(project, userId)) {
            throw new RuntimeException("Access denied. You are not a member of this project.");
        }

        return project.getMembers().stream()
                .map(this::mapUserToMemberResponse)
                .collect(Collectors.toList());
    }

    /**
     * Search projects by name
     * @param searchTerm - search term
     * @param userId - requesting user ID
     * @return list of matching projects (only projects user is member of)
     */
    @Transactional(readOnly = true)
    public List<ProjectResponseDto> searchProjects(String searchTerm, Long userId) {
        List<Project> userProjects = projectRepository.findProjectsByMemberId(userId);

        return userProjects.stream()
                .filter(project -> project.getName().toLowerCase().contains(searchTerm.toLowerCase()))
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    // Helper Methods

    /**
     * Check if user is a member of the project
     * @param project - project entity
     * @param userId - user ID
     * @return true if user is a member
     */
    private boolean isUserMemberOfProject(Project project, Long userId) {
        return project.getMembers().stream()
                .anyMatch(member -> member.getId().equals(userId));
    }

    /**
     * Validate project creation/update DTO
     * @param projectDto - project data to validate
     */
    private void validateProjectDto(ProjectCreateDto projectDto) {
        if (!projectDto.isDateRangeValid()) {
            throw new IllegalArgumentException("End date must be after start date");
        }
    }

    /**
     * Convert Project entity to response DTO
     * @param project - project entity
     * @return project response DTO
     */
    private ProjectResponseDto convertToResponseDto(Project project) {
        ProjectResponseDto dto = new ProjectResponseDto();

        // Basic project info
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setDescription(project.getDescription());
        dto.setStartDate(project.getStartDate());
        dto.setEndDate(project.getEndDate());
        dto.setIsActive(project.getIsActive());
        dto.setCreatedAt(project.getCreatedAt());
        dto.setUpdatedAt(project.getUpdatedAt());

        // Owner info
        dto.setOwnerId(project.getOwner().getId());
        dto.setOwnerName(project.getOwner().getFullName());
        dto.setOwnerEmail(project.getOwner().getEmail());

        // Statistics
        dto.setMemberCount(project.getMembers().size());
        dto.setTaskCount(project.getTaskCount());
        dto.setCompletionPercentage(project.getCompletionPercentage());
        dto.setCompletedTasks(project.getCompletedTaskCount());
        dto.setPendingTasks(project.getTaskCount() - project.getCompletedTaskCount());

        // Health indicators
        if (project.getEndDate() != null) {
            dto.setIsOverdue(project.getEndDate().isBefore(LocalDate.now()) && project.getIsActive());
            dto.setDaysUntilDeadline(ChronoUnit.DAYS.between(LocalDate.now(), project.getEndDate()));
        }

        // Members (for detailed view)
        dto.setMembers(project.getMembers().stream()
                .map(this::mapUserToMemberResponse)
                .collect(Collectors.toList()));

        return dto;
    }

    /**
     * Map user to member response format
     * @param user - user entity
     * @return member response map
     */
    private Map<String, Object> mapUserToMemberResponse(User user) {
        Map<String, Object> memberMap = new HashMap<>();
        memberMap.put("id", user.getId());
        memberMap.put("firstName", user.getFirstName());
        memberMap.put("lastName", user.getLastName());
        memberMap.put("fullName", user.getFullName());
        memberMap.put("email", user.getEmail());
        memberMap.put("role", user.getRole());
        return memberMap;
    }
}