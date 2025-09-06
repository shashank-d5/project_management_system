package com.projectmanager.pms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Temporary Security Configuration
 * We're disabling security for now so we can test our basic setup
 * Later we'll replace this with proper JWT authentication
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configure HTTP security - temporarily disable all security
     * WARNING: This is only for development/testing!
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF (Cross-Site Request Forgery) protection for APIs
                .csrf(csrf -> csrf.disable())

                // Allow all requests without authentication
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()  // Allow all requests for now
                );

        return http.build();
    }
}