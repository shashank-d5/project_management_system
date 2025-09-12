package com.projectmanager.pms.exception;

/**
 * Custom exception thrown when project is not found
 */
public class ProjectNotFoundException extends RuntimeException {

    public ProjectNotFoundException(String message) {
        super(message);
    }

    public ProjectNotFoundException(Long projectId) {
        super("Project not found with ID: " + projectId);
    }

    public ProjectNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}