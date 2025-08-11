package com.example.taskmanagement_backend.services;

import com.example.taskmanagement_backend.dtos.OrganizationDto.CreateOrganizationRequestDto;
import com.example.taskmanagement_backend.dtos.OrganizationDto.OrganizationResponseDto;
import com.example.taskmanagement_backend.dtos.OrganizationDto.UpdateOrganizationRequestDto;
import com.example.taskmanagement_backend.entities.Organization;
import com.example.taskmanagement_backend.entities.User;
import com.example.taskmanagement_backend.repositories.OrganizationJpaRepository;
import com.example.taskmanagement_backend.repositories.UserJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrganizationService {
    @Autowired
    private OrganizationJpaRepository organizationJpaRepository;
    @Autowired
    private UserJpaRepository userJpaRepository;

    public List<OrganizationResponseDto> getAllOrganizations() {
        return organizationJpaRepository.findAll()
                .stream()
                .map(this::convertDto)
                .collect(Collectors.toList());
    }

    public OrganizationResponseDto getOrganizationById(Long id) {
        return organizationJpaRepository.findById(id)
                .map(this::convertDto)
                .orElse(null);
    }

    public OrganizationResponseDto createOrganization(CreateOrganizationRequestDto dto) {
        User owner = userJpaRepository.findById(dto.getOwner_id())
                .orElseThrow(() -> new IllegalArgumentException("Owner not found with id: " + dto.getOwner_id()));

        Organization org = Organization.builder()
                .name(dto.getName())
                .owner(owner)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return convertDto(organizationJpaRepository.save(org));
    }

    public OrganizationResponseDto updateOrganizations(Long id, UpdateOrganizationRequestDto dto) {
        Organization org = organizationJpaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Organization not found with id: " + id));

        User owner = userJpaRepository.findById(dto.getOwner_id())
                .orElseThrow(() -> new IllegalArgumentException("Owner not found with id: " + dto.getOwner_id()));

        org.setName(dto.getName());
        org.setOwner(owner); // cập nhật owner nếu cho phép
        org.setUpdatedAt(LocalDateTime.now());

        return convertDto(organizationJpaRepository.save(org));
    }

    public void deleteOrganization(Long id) {
        organizationJpaRepository.deleteById(id);
    }

    private OrganizationResponseDto convertDto(Organization org) {
        return new OrganizationResponseDto(
                org.getId(),
                org.getName(),
                org.getCreatedAt(),
                org.getUpdatedAt(),
                org.getOwner() != null ? org.getOwner().getId() : null
        );
    }
    public OrganizationResponseDto getOrganizationByOwnerId (Long ownerId) {
             return organizationJpaRepository.findByOwnerId(ownerId)
                     .map(this::convertDto)
                     .orElse(null);
    }
}
