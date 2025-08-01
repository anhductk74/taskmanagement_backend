package com.example.taskmanagement_backend.services;

import com.example.taskmanagement_backend.dtos.UserDto.CreateUserRequestDto;
import com.example.taskmanagement_backend.dtos.UserDto.UpdateUserRequestDto;
import com.example.taskmanagement_backend.dtos.UserDto.UserResponseDto;
import com.example.taskmanagement_backend.entities.Organization;
import com.example.taskmanagement_backend.entities.Role;
import com.example.taskmanagement_backend.entities.User;
import com.example.taskmanagement_backend.repositories.OrganizationJpaRepository;
import com.example.taskmanagement_backend.repositories.RoleJpaRepository;
import com.example.taskmanagement_backend.repositories.UserJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserJpaRepository userJpaRepository;
    private  RoleJpaRepository roleRepository;
    private  OrganizationJpaRepository organizationRepository;
    public UserService(UserJpaRepository userJpaRepository,  RoleJpaRepository roleRepository, OrganizationJpaRepository organizationRepository) {
        this.userJpaRepository = userJpaRepository;
        this.roleRepository = roleRepository;
        this.organizationRepository = organizationRepository;
    }
    public User createUser(CreateUserRequestDto dto) {
        User user = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(dto.getPassword()) // Bạn nên mã hóa mật khẩu thực tế
                .avt_url(dto.getAvt_url())
                .deleted(dto.getDeleted() != null ? dto.getDeleted() : false)
                .status(dto.getStatus())
                .build();
        if (dto.getRoleNames() != null) {
            Set<Role> roles = dto.getRoleNames().stream()
                    .map(roleName -> roleRepository.findByName(roleName).orElse(null))
                    .filter(role -> role != null)
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }
        if (dto.getOrganizationId() != null) {
            Optional<Organization> org = organizationRepository.findById(Long.valueOf(dto.getOrganizationId()));
            org.ifPresent(user::setOrganization);
        }

        return userJpaRepository.save(user);
    }
    public User updateUser(Integer id, UpdateUserRequestDto dto) {
        User user = userJpaRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setAvt_url(dto.getAvt_url());
        user.setDeleted(dto.getDeleted());
        user.setStatus(dto.getStatus());

        // Update Role
        if (dto.getRoleNames() != null) {
            Set<Role> roles = dto.getRoleNames().stream()
                    .map(roleName -> roleRepository.findByName(roleName)
                            .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }

        // Update Organization
        if (dto.getOrganizationId() != null) {
            Organization org = organizationRepository.findById(Long.valueOf(dto.getOrganizationId()))
                    .orElseThrow(() -> new RuntimeException("Organization not found"));
            user.setOrganization(org);
        }

        return userJpaRepository.save(user);
    }
    public UserResponseDto getUserById(Integer id) {
        User user = userJpaRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return convertToDto(user);
    }
    public List<UserResponseDto> getAllUsers() {
        return userJpaRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    public void deleteUser(Integer id) {
        if (!userJpaRepository.existsById(Long.valueOf(id))) {
            throw new EntityNotFoundException("User not found");
        }
        userJpaRepository.deleteById(Long.valueOf(id));
    }

    private UserResponseDto convertToDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .avt_url(user.getAvt_url())
                .deleted(user.isDeleted())
                .status(user.getStatus())
                .roleNames(user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                .organizationName(user.getOrganization() != null ? user.getOrganization().getName() : null)
                .build();
    }

    public List<UserResponseDto> findUserByUsername(String username) {
        return userJpaRepository.findByUsernameContainingIgnoreCase(username)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList()); // hoặc throw exception nếu không thấy
    }


}
