package com.example.taskmanagement_backend.services;



import com.example.taskmanagement_backend.dtos.ProjectInvitatinDto.CreateProjectInvitationRequestDto;
import com.example.taskmanagement_backend.dtos.ProjectInvitatinDto.ProjectInvitationResponseDto;
import com.example.taskmanagement_backend.dtos.ProjectInvitatinDto.UpdateProjectInvitationStatusRequestDto;
import com.example.taskmanagement_backend.entities.Project;
import com.example.taskmanagement_backend.entities.ProjectInvitation;
import com.example.taskmanagement_backend.entities.User;
import com.example.taskmanagement_backend.enums.InvitationStatus;
import com.example.taskmanagement_backend.repositories.ProjectInvitationRepository;

import com.example.taskmanagement_backend.repositories.ProjectJpaRepository;
import com.example.taskmanagement_backend.repositories.UserJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectInvitationService {

    private final ProjectInvitationRepository invitationRepository;
    private final ProjectJpaRepository projectRepository;
    private final UserJpaRepository userRepository;

    public ProjectInvitationResponseDto createInvitation(CreateProjectInvitationRequestDto dto) {
        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new EntityNotFoundException("Project không tồn tại"));

        User invitedBy = userRepository.findById(dto.getInvitedById())
                .orElseThrow(() -> new EntityNotFoundException("User mời không tồn tại"));

        ProjectInvitation invitation = ProjectInvitation.builder()
                .email(dto.getEmail())
                .project(project)
                .invitedBy(invitedBy)
                .status(InvitationStatus.PENDING)
                .token(java.util.UUID.randomUUID().toString())
                .createdAt(LocalDateTime.now())
                .build();

        invitationRepository.save(invitation);
        return toDto(invitation);
    }

    public List<ProjectInvitationResponseDto> getInvitationsByProject(Long projectId) {
        return invitationRepository.findByProjectId(projectId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public ProjectInvitationResponseDto updateStatus(Long invitationId, UpdateProjectInvitationStatusRequestDto dto) {
        ProjectInvitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new EntityNotFoundException("Invitation không tồn tại"));

        invitation.setStatus(dto.getStatus());
        invitationRepository.save(invitation);

        return toDto(invitation);
    }

    private ProjectInvitationResponseDto toDto(ProjectInvitation entity) {
        return ProjectInvitationResponseDto.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .projectId(entity.getProject().getId())
                .projectName(entity.getProject().getName())
                .invitedById(entity.getInvitedBy().getId())
                .invitedByName(entity.getInvitedBy().getUserProfile().getFirstName() + entity.getInvitedBy().getUserProfile().getLastName())
                .status(entity.getStatus())
                .token(entity.getToken())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
