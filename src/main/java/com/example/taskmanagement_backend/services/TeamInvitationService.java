package com.example.taskmanagement_backend.services;

import com.example.taskmanagement_backend.dtos.ProjectInvitatinDto.ProjectInvitationResponseDto;
import com.example.taskmanagement_backend.dtos.ProjectInvitatinDto.UpdateProjectInvitationStatusRequestDto;
import com.example.taskmanagement_backend.dtos.TeamInvitationDto.CreateTeamInvitationRequestDto;
import com.example.taskmanagement_backend.dtos.TeamInvitationDto.TeamInvitationResponseDto;
import com.example.taskmanagement_backend.dtos.TeamInvitationDto.UpdateTeamInvitationStatusRequestDto;
import com.example.taskmanagement_backend.entities.ProjectInvitation;
import com.example.taskmanagement_backend.entities.Team;
import com.example.taskmanagement_backend.entities.TeamInvitation;
import com.example.taskmanagement_backend.entities.User;
import com.example.taskmanagement_backend.enums.InvitationStatus;
import com.example.taskmanagement_backend.repositories.TeamInvatationJpaRepository;
import com.example.taskmanagement_backend.repositories.TeamJpaRepository;
import com.example.taskmanagement_backend.repositories.UserJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamInvitationService {
    private final TeamInvatationJpaRepository teamInvatationJpaRepository;
    private final TeamJpaRepository teamJpaRepository;
    private UserJpaRepository userJpaRepository;

    public TeamInvitationResponseDto createTeamInvitation(CreateTeamInvitationRequestDto dto) {
        Team team = teamJpaRepository.findById(dto.getTeamId())
                .orElseThrow(() -> new EntityNotFoundException("Project không tồn tại"));
        User invitedBy = userJpaRepository.findById(dto.getInvitedById())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        TeamInvitation invitation = TeamInvitation.builder()
                .email(dto.getEmail())
                .team(team)
                .invitedBy(invitedBy)
                .status(InvitationStatus.PENDING)
                .token(java.util.UUID.randomUUID().toString())
                .createdAt(LocalDateTime.now())
                .build();
            teamInvatationJpaRepository.save(invitation);
        return toDto(invitation);
    }
    public List<TeamInvitationResponseDto> getTeamInvitationsByTeam(Long teamId) {
        return teamInvatationJpaRepository.findByTeamId(teamId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    public TeamInvitationResponseDto updateStatus(Long invitationId, UpdateTeamInvitationStatusRequestDto dto) {
        TeamInvitation invitation = teamInvatationJpaRepository.findById(invitationId)
                .orElseThrow(() -> new EntityNotFoundException("Invitation không tồn tại"));

        invitation.setStatus(dto.getStatus());
        teamInvatationJpaRepository.save(invitation);

        return toDto(invitation);
    }
    private TeamInvitationResponseDto toDto(TeamInvitation entity) {
        return TeamInvitationResponseDto.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .teamId(entity.getTeam().getId())
                .teamName(entity.getTeam().getName())
                .invitedById(entity.getInvitedBy().getId())
                .invitedByName(entity.getInvitedBy().getUserProfile().getFirstName() + entity.getInvitedBy().getUserProfile().getLastName())
                .status(entity.getStatus())
                .token(entity.getToken())
                .createdAt(entity.getCreatedAt())
                .build();
    }

}
