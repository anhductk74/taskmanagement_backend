package com.example.taskmanagement_backend.services;

import com.example.taskmanagement_backend.dtos.OrganizationDto.CreateOrganizationRequestDto;
import com.example.taskmanagement_backend.dtos.OrganizationDto.OrganizationResponseDto;
import com.example.taskmanagement_backend.dtos.OrganizationDto.UpdateOrganizationRequestDto;
import com.example.taskmanagement_backend.entities.Organization;
import com.example.taskmanagement_backend.repositories.OrganizationJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrganizationService {
    @Autowired
    private OrganizationJpaRepository organizationJpaRepository;

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
        Organization org = new Organization();
        org.setName(dto.getName());
        org.setCreatedAt(LocalDateTime.now());
        org.setUpdatedAt(LocalDateTime.now());
        return convertDto(organizationJpaRepository.save(org));
    }

    public OrganizationResponseDto updateOrganizations(Long id, UpdateOrganizationRequestDto dto) {
        Organization org = organizationJpaRepository.findById(id).orElseThrow();
        org.setName(dto.getName());
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
                org.getUpdatedAt()
        );
    }
}
