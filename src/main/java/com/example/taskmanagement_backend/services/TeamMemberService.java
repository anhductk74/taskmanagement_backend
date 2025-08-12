package com.example.taskmanagement_backend.services;

import com.example.taskmanagement_backend.dtos.ProjectMemberDto.ProjectMemberResponseDto;
import com.example.taskmanagement_backend.dtos.ProjectMemberDto.UpdateProjectMemberRequestDto;
import com.example.taskmanagement_backend.dtos.TeamMemberDto.CreateTeamMemberRequestDto;
import com.example.taskmanagement_backend.dtos.TeamMemberDto.TeamMemberResponseDto;
import com.example.taskmanagement_backend.dtos.TeamMemberDto.UpdateTeamMemberRequestDto;
import com.example.taskmanagement_backend.entities.ProjectMember;
import com.example.taskmanagement_backend.entities.Team;
import com.example.taskmanagement_backend.entities.TeamMember;
import com.example.taskmanagement_backend.entities.User;
import com.example.taskmanagement_backend.repositories.ProjectJpaRepository;

import com.example.taskmanagement_backend.repositories.TeamJpaRepository;
import com.example.taskmanagement_backend.repositories.TeamMemberJpaRepository;
import com.example.taskmanagement_backend.repositories.UserJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TeamMemberService {
    @Autowired
    private TeamMemberJpaRepository teamMemberJpaRepository;
    @Autowired
    private TeamJpaRepository teamJpaRepository;
    @Autowired
    private UserJpaRepository userRepository;

    public List<TeamMemberResponseDto> getMembersByTeam(Long teamId) {
        return teamMemberJpaRepository.findByTeamId(teamId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

    }
    public TeamMemberResponseDto createTeamMember(CreateTeamMemberRequestDto dto) {
        Team team = teamJpaRepository.findById(dto.getTeamId())
                .orElseThrow(()-> new RuntimeException("team not found with id: " + dto.getTeamId()));
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + dto.getUserId()));
        TeamMember teamMember = TeamMember.builder()
                .team(team)
                .user(user)
                .roleId(dto.getRoleId())
                .joinedAt(LocalDateTime.now())
                .build();
        return  convertToDto(teamMemberJpaRepository.save(teamMember));


    }
    public void deleteTeamMember(Long id) {
        if (!teamMemberJpaRepository.existsById(id)) {
            throw new RuntimeException("Team member not found with id: " + id);
        }
        teamMemberJpaRepository.deleteById(id);
    }
    public TeamMemberResponseDto updateTeamMember(Long id, UpdateTeamMemberRequestDto dto) {
        TeamMember teamMember = teamMemberJpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team member not found with id: " + id));

        teamMember.setRoleId(dto.getRoleId());

        return convertToDto(teamMemberJpaRepository.save(teamMember));
    }

    private TeamMemberResponseDto convertToDto(TeamMember entity) {
        return TeamMemberResponseDto.builder()
                .id(entity.getId())
                .teamId(entity.getTeam().getId())
                .userId(entity.getUser().getId())
                .roleId(entity.getRoleId())
                .joinedAt(entity.getJoinedAt())
                .build();
    }


}
