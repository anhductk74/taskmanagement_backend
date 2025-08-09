package com.example.taskmanagement_backend.controllers;

import com.example.taskmanagement_backend.dtos.OAuth2Dto.RefreshTokenRequestDto;
import com.example.taskmanagement_backend.dtos.OAuth2Dto.TokenResponseDto;
import com.example.taskmanagement_backend.dtos.UserDto.LoginRequestDto;
import com.example.taskmanagement_backend.dtos.UserDto.LoginResponseDto;
import com.example.taskmanagement_backend.services.TokenRefreshService;
import com.example.taskmanagement_backend.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
@Slf4j
@Tag(name = "Authentication", description = "Authentication endpoints")
public class AuthController {

    private final UserService userService;
    private final TokenRefreshService tokenRefreshService;

    @PostMapping("/login")
    @Operation(summary = "Login with email and password", description = "Traditional login with email and password")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request) throws Exception {
        LoginResponseDto result = this.userService.login(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Get new access token using refresh token")
    public ResponseEntity<TokenResponseDto> refreshToken(
            @Valid @RequestBody RefreshTokenRequestDto request,
            HttpServletRequest httpRequest) {
        
        log.info("Processing token refresh request");
        
        String deviceInfo = extractDeviceInfo(httpRequest);
        TokenResponseDto response = tokenRefreshService.refreshAccessToken(
                request.getRefreshToken(), 
                deviceInfo
        );
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Revoke refresh token and logout user")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequestDto request) {
        log.info("Processing logout request");
        tokenRefreshService.revokeRefreshToken(request.getRefreshToken());
        return ResponseEntity.ok().build();
    }

    private String extractDeviceInfo(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        String clientIp = getClientIpAddress(request);
        return String.format("IP: %s, UserAgent: %s", clientIp, userAgent);
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}