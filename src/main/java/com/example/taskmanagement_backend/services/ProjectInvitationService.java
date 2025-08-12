package com.example.taskmanagement_backend.services;


import com.example.taskmanagement_backend.dtos.ProjectInvitatinDto.CreateProjectInvitationRequestDto;
import com.example.taskmanagement_backend.dtos.ProjectInvitatinDto.ProjectInvitationResponseDto;
import com.example.taskmanagement_backend.dtos.ProjectInvitatinDto.UpdateProjectInvitationStatusRequestDto;
import com.example.taskmanagement_backend.entities.*;
import com.example.taskmanagement_backend.enums.InvitationStatus;
import com.example.taskmanagement_backend.repositories.*;

import jakarta.mail.MessagingException;
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
    private final ProjectMemberJpaRepository projectMemberRepository;
    private final UserJpaRepository userRepository;
    private final EmailService emailService;
    private  final RoleJpaRepository roleRepository;

    public ProjectInvitationResponseDto createInvitation(CreateProjectInvitationRequestDto dto) throws MessagingException {
        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new EntityNotFoundException("Project không tồn tại"));

        User invitedBy = userRepository.findById(dto.getInvitedById())
                .orElseThrow(() -> new EntityNotFoundException("User mời không tồn tại"));

        User userInvite = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User được mời không tồn tại"));

        Role role = roleRepository.findById(dto.getRoleId())
                .orElseThrow(()-> new EntityNotFoundException("User không tồn tại"));

        ProjectInvitation invitation = ProjectInvitation.builder()
                .email(dto.getEmail())
                .project(project)
                .invitedBy(invitedBy)
                .status(InvitationStatus.PENDING)
                .role(role)
                .token(java.util.UUID.randomUUID().toString())
                .createdAt(LocalDateTime.now())
                .build();

        invitationRepository.save(invitation);
        // Tạo link accept
        String inviteLink = "localhost:8080/api/invitations/accept?token=" + invitation.getToken();

        // Gửi email
        emailService.sendInvitationEmail(dto.getEmail(), invitation.getProject().getName(), inviteLink);
        return toDto(invitation);
    }

    public String acceptInvitation(String token) {
        ProjectInvitation invitation = invitationRepository.findByToken(token)
                .orElseThrow(() -> new EntityNotFoundException("Token không hợp lệ"));

        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new IllegalStateException("Lời mời đã hết hạn hoặc đã xử lý trước đó");
        }

        // Tìm user theo email
        User user = userRepository.findByEmail(invitation.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("Người dùng không tồn tại"));

        // Cập nhật status
        invitation.setStatus(InvitationStatus.ACCEPTED);
        invitationRepository.save(invitation);

        // Thêm vào bảng project_members
        ProjectMember member = ProjectMember.builder()
                .project(invitation.getProject())
                .user(user)
                .roleId(invitation.getRole().getId())
                .joinedAt(LocalDateTime.now())
                .build();

        projectMemberRepository.save(member);

        return "Lời mời đã được chấp nhận";
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
