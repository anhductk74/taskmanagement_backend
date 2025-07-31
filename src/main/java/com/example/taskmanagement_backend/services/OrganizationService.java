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
                .map(organization ->
                        new OrganizationResponseDto(organization.getId(),organization.getName(),
                                organization.getCreatedAt(),organization.getUpdatedAt())).collect(Collectors.toList());
    }

    public OrganizationResponseDto getOrganizationById(Long id) {
        Organization organization = organizationJpaRepository.findById(id).orElse(null);
        return new OrganizationResponseDto(organization.getId(),organization.getName(),organization.getCreatedAt(),organization.getUpdatedAt());
    }

    public OrganizationResponseDto createOrganization(CreateOrganizationRequestDto organizationDto) {
        Organization organization = new Organization();
        organization.setName(organizationDto.getName());
        organization.setCreatedAt(LocalDateTime.now());
        organization.setUpdatedAt(LocalDateTime.now());
        Organization savedOrganization = organizationJpaRepository.save(organization);
        return new OrganizationResponseDto(savedOrganization.getId(), savedOrganization.getName(), savedOrganization.getCreatedAt(), savedOrganization.getUpdatedAt());
    }

    public void deleteOrganization(Long id) {
        this.organizationJpaRepository.deleteById(id);
    }

    public OrganizationResponseDto updateOrganizations(Long id, UpdateOrganizationRequestDto updateOrganizationRequestDto) {
        OrganizationResponseDto organizationDto = getOrganizationById(id);
        Organization organization = new Organization(organizationDto.getId(), organizationDto.getName(), organizationDto.getCreatedAt(), organizationDto.getUpdatedAt());
        organization.setName(updateOrganizationRequestDto.getName());
        organization.setUpdatedAt(LocalDateTime.now());
        Organization savedOrganization = organizationJpaRepository.save(organization);
        return new OrganizationResponseDto(savedOrganization.getId(), savedOrganization.getName(), savedOrganization.getCreatedAt(), savedOrganization.getUpdatedAt());
    }
}
