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



import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity()
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptionHandlingCustomizer -> exceptionHandlingCustomizer
                        .authenticationEntryPoint(this.customAuthenticationEntryPoint)
                        .accessDeniedHandler(this.customAccessDeniedHandler))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/api/users/**").hasAnyRole("owner", "pm")
                        .requestMatchers("/api/tasks/**").hasAnyRole("admin","owner", "pm")
                        .requestMatchers(HttpMethod.GET, "/api/projects/{id}").hasRole("PM")
                        .requestMatchers(HttpMethod.PUT, "/api/projects/{id}").hasRole("PM")
                        .requestMatchers("/api/projects/**").hasAnyRole("admin","owner")
                        .requestMatchers(HttpMethod.GET, "/api/organizations/{id}").hasRole("owner")
                        .requestMatchers(HttpMethod.PUT, "/api/organizations/{id}").hasRole("owner")
                        .requestMatchers(HttpMethod.DELETE, "/api/organizations/{id}").hasRole("owner")
                        .requestMatchers("/api/organizations/**").hasAnyRole("admin")
                        .anyRequest().permitAll())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
