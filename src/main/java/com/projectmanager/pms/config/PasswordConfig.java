package com.projectmanager.pms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration class for password encoding
 * Uses BCrypt hashing algorithm for secure password storage
 */
@Configuration
public class PasswordConfig {

    /**
     * Password encoder bean using BCrypt
     * BCrypt is a strong hashing function designed for passwords
     * It includes salt generation and is resistant to brute-force attacks
     *
     * @return BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt with default strength (10 rounds)
        // Higher rounds = more secure but slower
        // 10 is a good balance between security and performance
        return new BCryptPasswordEncoder();
    }
}












