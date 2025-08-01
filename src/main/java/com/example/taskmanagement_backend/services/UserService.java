package com.example.taskmanagement_backend.services;

import com.example.taskmanagement_backend.dtos.UserDto.CreateUserRequestDto;
import com.example.taskmanagement_backend.dtos.UserDto.UpdateUserRequestDto;
import com.example.taskmanagement_backend.dtos.UserDto.UserResponseDto;
import com.example.taskmanagement_backend.entities.Organization;
import com.example.taskmanagement_backend.entities.Role;
import com.example.taskmanagement_backend.entities.User;
import com.example.taskmanagement_backend.entities.UserProfile;
import com.example.taskmanagement_backend.repositories.OrganizationJpaRepository;
import com.example.taskmanagement_backend.repositories.RoleJpaRepository;
import com.example.taskmanagement_backend.repositories.UserJpaRepository;
import com.example.taskmanagement_backend.repositories.UserProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private UserJpaRepository userRepository;
    private RoleJpaRepository roleRepository;
    private OrganizationJpaRepository organizationRepository;

    public UserService(UserJpaRepository userRepository, RoleJpaRepository roleRepository, OrganizationJpaRepository organizationRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.organizationRepository = organizationRepository;
    }

    public UserResponseDto createUser(CreateUserRequestDto dto) {
        // Lấy danh sách role từ ID
        Set<Role> roles = roleRepository.findAllById(dto.getRoleIds())
                .stream()
                .map(role -> (Role) role)
                .collect(Collectors.toSet());

        // Tìm organization từ ID
        Organization organization = organizationRepository.findById(Long.valueOf(dto.getOrganizationId()))
                .orElseThrow(() -> new EntityNotFoundException("Organization not found"));

        // Tạo user
        User user = User.builder()
                .email(dto.getEmail())
                .password(dto.getPassword())
                .roles(roles)
                .organization(organization)
                .firstLogin(true)
                .deleted(false) // Mặc định false
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Lưu user và trả kết quả
        return convertToDto(userRepository.save(user));
    }

    public UserResponseDto updateUser(Integer id, UpdateUserRequestDto dto) {
        User user = userRepository.findById(Long.valueOf(id)).orElseThrow(() -> new RuntimeException("User not found"));

        user.setEmail(dto.getEmail());
        user.setFirstLogin(dto.isFirstLogin());
        user.setUpdatedAt(LocalDateTime.now());

        if (dto.getOrganizationId() != null) {
            Organization organization = organizationRepository.findById(Long.valueOf(dto.getOrganizationId())).orElse(null);
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

    public void deleteUser(Integer id) {
        User user = userRepository.findById(Long.valueOf(id)).orElseThrow(() -> new RuntimeException("User not found"));
        user.setDeleted(true);
        userRepository.save(user);
    }

    public UserResponseDto getUserById(Integer id) {
        User user = userRepository.findById(Long.valueOf(id)).orElseThrow(() -> new RuntimeException("User not found"));
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
