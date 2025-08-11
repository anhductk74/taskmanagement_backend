package com.example.taskmanagement_backend.config;

import com.example.taskmanagement_backend.exceptions.CustomAccessDeniedHandler;
import com.example.taskmanagement_backend.exceptions.CustomAuthenticationEntryPoint;
import com.example.taskmanagement_backend.filters.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {}) // dùng corsFilter bean phía dưới
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/organizations").permitAll()

                        // Users
                        .requestMatchers("/api/users/**").hasAnyRole("owner", "pm")

                        // Tasks
                        .requestMatchers("/api/tasks/**").hasAnyRole("admin", "owner", "pm")

                        // Projects
                        .requestMatchers(HttpMethod.GET, "/api/projects/{id}").hasRole("PM")
                        .requestMatchers(HttpMethod.PUT, "/api/projects/{id}").hasRole("PM")
                        .requestMatchers("/api/projects/**").hasAnyRole("admin", "owner")

                        // Organizations
                        .requestMatchers(HttpMethod.GET, "/api/organizations/{id}").hasRole("owner")
                        .requestMatchers(HttpMethod.PUT, "/api/organizations/{id}").hasRole("owner")
                        .requestMatchers(HttpMethod.DELETE, "/api/organizations/{id}").hasRole("owner")
                        .requestMatchers(HttpMethod.GET, "/api/organizations/by-owner/{id}").hasRole("owner")
                        .requestMatchers("/api/organizations/**").hasAnyRole("admin")

                        //invatation
                        .requestMatchers("/api/project-invitations/**").hasAnyRole("owner")
                        .requestMatchers("/api/team-invitations/**").hasAnyRole("owner", "pm")

                        // Anything else
                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS configuration (if needed)
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000")); // or "*" for all
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true); // important if using cookies or Authorization header

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
