package com.projectmanager.pms.config;

import org.springframework.beans.factory.annotation.Autowired; // You might not need this import anymore
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 1. REMOVE THIS ENTIRE FIELD
    // @Autowired
    // private JwtAuthenticationFilter jwtAuthenticationFilter;

    // In SecurityConfig.java

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // CORRECTED AUTHORIZATION RULES
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()        // REMOVED /api
                        .requestMatchers("/test/**").permitAll()        // REMOVED /api
                        .requestMatchers("/data-test/**").permitAll()   // REMOVED /api
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/users/**").authenticated()   // REMOVED /api
                        .requestMatchers("/projects/**").authenticated()// REMOVED /api
                        .requestMatchers("/tasks/**").authenticated()   // REMOVED /api
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}