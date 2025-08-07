package com.example.taskmanagement_backend.services;

import com.example.taskmanagement_backend.dtos.UserDto.*;
import com.example.taskmanagement_backend.dtos.UserProfileDto.UserProfileResponseDto;
import com.example.taskmanagement_backend.entities.Organization;
import com.example.taskmanagement_backend.entities.Role;
import com.example.taskmanagement_backend.entities.User;
import com.example.taskmanagement_backend.entities.UserProfile;
import com.example.taskmanagement_backend.exceptions.DuplicateEmailException;
import com.example.taskmanagement_backend.exceptions.HttpException;
import com.example.taskmanagement_backend.repositories.OrganizationJpaRepository;
import com.example.taskmanagement_backend.repositories.RoleJpaRepository;
import com.example.taskmanagement_backend.repositories.UserJpaRepository;
import com.example.taskmanagement_backend.repositories.UserProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserJpaRepository userRepository;
    private final RoleJpaRepository roleRepository;
    private final OrganizationJpaRepository organizationRepository;
    private final JwtService jwtService;
    private final UserProfileRepository userProfileRepository;
    private final UserProfileService  userProfileService;

    //    public UserService(UserJpaRepository userRepository, RoleJpaRepository roleRepository, OrganizationJpaRepository organizationRepository,  JwtService jwtService ) {
//        this.userRepository = userRepository;
//        this.roleRepository = roleRepository;
//        this.organizationRepository = organizationRepository;
//        this.jwtService = jwtService;
//    }
    public LoginResponseDto login(LoginRequestDto request) throws Exception {
        // Find the user by email (username)
        User user = this.userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new HttpException("Invalid username or password", HttpStatus.UNAUTHORIZED));

        // Verify password
        if (!request.getPassword().equals(user.getPassword())) {
            throw new HttpException("Invalid username or password", HttpStatus.UNAUTHORIZED);
        }
        UserProfileResponseDto userProfileResponseDto = userProfileService.getUserProfile(user.getId());
        List<Role> roles = new ArrayList<>(user.getRoles());
        // Generate a new access token (with full data + roles)
        String accessToken = jwtService.generateAccessToken(user,userProfileResponseDto);

        return LoginResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .roles(roles)
                .profile(userProfileResponseDto)
                .accessToken(accessToken)
                .build();
    }




    public UserResponseDto createUser(CreateUserRequestDto dto) {
        // Lấy role
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateEmailException("Email already exists: " + dto.getEmail());
        }
        Set<Role> roles = roleRepository.findAllById(dto.getRoleIds())
                .stream()
                .map(role -> (Role) role)
                .collect(Collectors.toSet());

        // Lấy organization
        Organization organization = null;

        if (dto.getOrganizationId() != null) {
            organization = organizationRepository.findById(dto.getOrganizationId())
                    .orElseThrow(() -> new RuntimeException("Organization not found"));
        }
        // Tạo user
        User user = User.builder()
                .email(dto.getEmail())
                .password(dto.getPassword())
                .firstLogin(true)
                .deleted(false)
                .roles(roles)
                .organization(organization)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Lưu user trước để lấy ID
        User savedUser = userRepository.save(user);

        // Tạo UserProfile trống
        UserProfile profile = UserProfile.builder()
                .user(savedUser)
                .firstName("")
                .lastName("")
                .status("active")
                .avtUrl("")
                .build();

        savedUser.setUserProfile(profile);
        userProfileRepository.save(profile);

        return convertToDto(savedUser);
    }

    public UserResponseDto updateUser(Long id, UpdateUserRequestDto dto) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        user.setEmail(dto.getEmail());
        user.setFirstLogin(dto.isFirstLogin());
        user.setUpdatedAt(LocalDateTime.now());

        if (dto.getOrganizationId() != null) {
            Organization organization = organizationRepository.findById(dto.getOrganizationId()).orElse(null);
            user.setOrganization(organization);
        }

        UserProfile profile = user.getUserProfile();
        if (profile != null) {
            profile.setFirstName(dto.getFirstName());
            profile.setLastName(dto.getLastName());
            profile.setStatus(dto.getStatus());
            profile.setAvtUrl(dto.getAvtUrl());
        }

        return convertToDto(userRepository.save(user));
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setDeleted(true);
        userRepository.save(user);
    }

    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        return convertToDto(user);
    }

    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .filter(u -> !u.isDeleted())
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public UserResponseDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        return convertToDto(user);
    }

    public UserResponseDto convertToDto(User user) {
        UserProfile profile = user.getUserProfile();

        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(profile != null ? profile.getFirstName() : null)
                .lastName(profile != null ? profile.getLastName() : null)
                .avt_url(profile != null ? profile.getAvtUrl() : null)
                .firstLogin(user.isFirstLogin())
                .deleted(user.isDeleted())
                .status(profile != null ? profile.getStatus() : null)
                .roleNames(user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                .organizationName(user.getOrganization() != null ? user.getOrganization().getName() : null)
                .build();
    }

}
