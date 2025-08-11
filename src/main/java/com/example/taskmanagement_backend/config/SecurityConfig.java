package com.example.taskmanagement_backend.config;


import com.example.taskmanagement_backend.exceptions.CustomAccessDeniedHandler;
import com.example.taskmanagement_backend.exceptions.CustomAuthenticationEntryPoint;
import com.example.taskmanagement_backend.filters.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity()
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - OAuth2 must be first
                        .requestMatchers("/api/auth/google/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        
                        // Swagger/OpenAPI endpoints
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        
                        // User registration
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/organizations").permitAll()

                        // Protected endpoints with role-based access
                        .requestMatchers("/api/users/**").hasAnyRole("OWNER", "PROJECT_MANAGER", "ADMIN")
                        .requestMatchers("/api/tasks/**").hasAnyRole("ADMIN", "OWNER", "PROJECT_MANAGER", "MEMBER", "LEADER")
                        .requestMatchers(HttpMethod.GET, "/api/projects/{id}").hasAnyRole("PROJECT_MANAGER", "OWNER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/projects/{id}").hasAnyRole("PROJECT_MANAGER", "OWNER")
                        .requestMatchers("/api/projects/**").hasAnyRole("ADMIN", "OWNER", "PROJECT_MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/organizations/{id}").hasAnyRole("OWNER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/organizations/{id}").hasAnyRole("OWNER")
                        .requestMatchers(HttpMethod.DELETE, "/api/organizations/{id}").hasRole("OWNER")
                        .requestMatchers("/api/organizations/**").hasAnyRole("ADMIN", "OWNER")
                        
                        // All other requests require authentication
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
