package com.projectmanager.pms.config;

import com.projectmanager.pms.service.UserService;
import com.projectmanager.pms.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter
 * This filter runs on every HTTP request to validate JWT tokens
 * If valid token is found, it sets the user in Spring Security context
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    /**
     * Filter method that runs on every HTTP request
     * Extracts JWT token from Authorization header and validates it
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            // Extract JWT token from Authorization header
            String jwt = extractJwtFromRequest(request);

            // If token exists and user is not already authenticated
            if (jwt != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Extract username from token
                String username = jwtUtil.extractUsername(jwt);

                if (username != null) {
                    // Load user details from database
                    UserDetails userDetails = userService.loadUserByUsername(username);

                    // Validate token against user details
                    if (jwtUtil.validateToken(jwt, userDetails)) {
                        // Create authentication token
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );

                        // Set additional details
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        // Set authentication in Spring Security context
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            }
        } catch (Exception e) {
            // Log the error but don't block the request
            // Invalid tokens will result in unauthenticated requests
            logger.error("Cannot set user authentication: {}");
        }

        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token from Authorization header
     * @param request - HTTP request
     * @return JWT token or null if not found/invalid format
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        // Check if header exists and starts with "Bearer "
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Remove "Bearer " prefix
        }

        return null;
    }

    /**
     * Determine if this filter should be applied to the request
     * We can skip certain paths like login, register, public endpoints
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        // Skip JWT validation for these paths
        return path.startsWith("/api/auth/login") ||
                path.startsWith("/api/auth/register") ||
                path.startsWith("/api/auth/health") ||
                path.startsWith("/api/test/") ||
                path.startsWith("/api/data-test/");
    }
}