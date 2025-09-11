package com.projectmanager.pms.service;

import com.projectmanager.pms.dto.AuthResponseDto;
import com.projectmanager.pms.dto.UserLoginDto;
import com.projectmanager.pms.dto.UserRegistrationDto;
import com.projectmanager.pms.entity.User;
import com.projectmanager.pms.enums.Role;
import com.projectmanager.pms.exception.EmailAlreadyExistsException;
import com.projectmanager.pms.exception.InvalidCredentialsException;
import com.projectmanager.pms.exception.UserNotFoundException;
import com.projectmanager.pms.repository.UserRepository;
import com.projectmanager.pms.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service class for user-related business logic
 * Implements UserDetailsService for Spring Security integration
 */
@Service
@Transactional
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Register a new user
     * @param registrationDto - user registration data
     * @return authentication response with JWT token
     */
    public AuthResponseDto registerUser(UserRegistrationDto registrationDto) {
        // Validate input
        validateRegistrationDto(registrationDto);

        // Check if email already exists
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new EmailAlreadyExistsException(registrationDto.getEmail());
        }

        // Create new user entity
        User user = new User();
        user.setFirstName(registrationDto.getFirstName().trim());
        user.setLastName(registrationDto.getLastName().trim());
        user.setEmail(registrationDto.getEmail().toLowerCase().trim());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setRole(Role.USER);  // Default role
        user.setIsActive(true);

        // Save user to database
        user = userRepository.save(user);

        // Generate JWT token
        UserDetails userDetails = loadUserByUsername(user.getEmail());
        String token = jwtUtil.generateToken(userDetails, user.getId(), user.getRole().name());

        // Return authentication response
        return AuthResponseDto.registrationSuccess(
                token,
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole()
        );
    }

    /**
     * Authenticate user login
     * @param loginDto - login credentials
     * @return authentication response with JWT token
     */
    public AuthResponseDto loginUser(UserLoginDto loginDto) {
        // Find user by email
        User user = userRepository.findByEmailAndIsActive(loginDto.getEmail().toLowerCase().trim(), true)
                .orElseThrow(() -> new InvalidCredentialsException());

        // Verify password
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        // Generate JWT token
        UserDetails userDetails = loadUserByUsername(user.getEmail());
        String token = jwtUtil.generateToken(userDetails, user.getId(), user.getRole().name());

        // Return authentication response
        return AuthResponseDto.success(
                token,
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole()
        );
    }

    /**
     * Get user by ID
     * @param userId - user ID
     * @return user entity
     */
    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    /**
     * Get user by email
     * @param email - user email
     * @return user entity
     */
    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmailAndIsActive(email, true)
                .orElseThrow(() -> new UserNotFoundException("email", email));
    }

    /**
     * Get all active users
     * @return list of active users
     */
    @Transactional(readOnly = true)
    public List<User> getAllActiveUsers() {
        return userRepository.findByIsActive(true);
    }

    /**
     * Search users by name
     * @param searchTerm - search term
     * @return list of matching users
     */
    @Transactional(readOnly = true)
    public List<User> searchUsersByName(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllActiveUsers();
        }
        return userRepository.findByNameContainingIgnoreCase(searchTerm.trim());
    }

    /**
     * Update user profile
     * @param userId - user ID
     * @param updateDto - updated user data
     * @return updated user
     */
    public User updateUserProfile(Long userId, UserRegistrationDto updateDto) {
        User user = getUserById(userId);

        // Update basic info (email update requires additional validation)
        user.setFirstName(updateDto.getFirstName().trim());
        user.setLastName(updateDto.getLastName().trim());

        // Only update email if it's different and not taken
        if (!user.getEmail().equals(updateDto.getEmail().toLowerCase().trim())) {
            if (userRepository.existsByEmail(updateDto.getEmail())) {
                throw new EmailAlreadyExistsException(updateDto.getEmail());
            }
            user.setEmail(updateDto.getEmail().toLowerCase().trim());
        }

        return userRepository.save(user);
    }

    /**
     * Change user password
     * @param userId - user ID
     * @param currentPassword - current password
     * @param newPassword - new password
     */
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = getUserById(userId);

        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * Deactivate user (soft delete)
     * @param userId - user ID
     */
    public void deactivateUser(Long userId) {
        User user = getUserById(userId);
        user.setIsActive(false);
        userRepository.save(user);
    }

    /**
     * Load user by username for Spring Security
     * @param username - username (email in our case)
     * @return UserDetails object
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmailAndIsActive(username, true)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getIsActive(),
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                getAuthorities(user.getRole())
        );
    }

    /**
     * Get authorities for Spring Security based on user role
     * @param role - user role
     * @return list of authorities
     */
    private List<org.springframework.security.core.GrantedAuthority> getAuthorities(Role role) {
        List<org.springframework.security.core.GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority(role.getAuthority()));
        return authorities;
    }

    /**
     * Validate registration DTO
     * @param registrationDto - registration data to validate
     */
    private void validateRegistrationDto(UserRegistrationDto registrationDto) {
        if (!registrationDto.isPasswordMatching()) {
            throw new IllegalArgumentException("Passwords do not match");
        }
    }
}
