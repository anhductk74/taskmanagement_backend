package com.example.taskmanagement_backend.services;

import com.example.taskmanagement_backend.dtos.TeamDto.CreateTeamResponseDto;
import com.example.taskmanagement_backend.dtos.TeamDto.TeamResponseDto;
import com.example.taskmanagement_backend.dtos.TeamDto.UpdateTeamResponseDto;
import com.example.taskmanagement_backend.entities.Project;
import com.example.taskmanagement_backend.entities.Team;
import com.example.taskmanagement_backend.entities.User;
import com.example.taskmanagement_backend.repositories.ProjectJpaRepository;
import com.example.taskmanagement_backend.repositories.TeamJpaRepository;
import com.example.taskmanagement_backend.repositories.UserJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TeamService {
    @Autowired
    private TeamJpaRepository  teamJpaRepository;
    @Autowired
    UserJpaRepository userJpaRepository;
    @Autowired
    ProjectJpaRepository projectJpaRepository;

    public List<TeamResponseDto> getAllTeams() {
        return teamJpaRepository.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public TeamResponseDto getTeamById(Long id) {
        return teamJpaRepository.findById(id).map(this::convertToDto).orElse(null);
    }

    public TeamResponseDto createTeams(CreateTeamResponseDto dto){
        Team team = Team.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .project(dto.getProject_id() != null ? getProject(dto.getProject_id()) : null)
                .leader(dto.getLeader_id() != null ? getUser(dto.getLeader_id()) : null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return convertToDto(teamJpaRepository.save(team));
    }

    public TeamResponseDto updateTeams(Long id, UpdateTeamResponseDto dto){
        Team team = convertToEntity(getTeamById(id));
        if(dto.getName() != null) team.setName(dto.getName());
        if(dto.getDescription() != null) team.setDescription(dto.getDescription());
        if(dto.getProjectId() != null) team.setProject(getProject(dto.getProjectId()));
        if(dto.getLeaderId() != null) team.setLeader(getUser(dto.getLeaderId()));
        team.setUpdatedAt(LocalDateTime.now());
        return convertToDto(teamJpaRepository.save(team));
    }

    public boolean deleteTeamById(Long id) {
        Optional<Team> team = teamJpaRepository.findById(id);
        if(team.isPresent()) {
            teamJpaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private TeamResponseDto convertToDto(Team team) {
        return TeamResponseDto.builder()
                .id(team.getId())
                .name(team.getName())
                .description(team.getDescription())
                .projectId(team.getProject() != null ? team.getProject().getId() : null)
                .leaderId(Long.valueOf(team.getLeader() != null ? team.getLeader().getId() : null))
                .createdAt(team.getCreatedAt())
                .updatedAt(team.getUpdatedAt())
        .build();
    }

    private Team convertToEntity(TeamResponseDto dto) {
        return Team.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .project(dto.getProjectId() != null ? getProject(dto.getProjectId()) : null)
                .leader(dto.getLeaderId() != null ? getUser(dto.getLeaderId()) : null)
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }

    private User getUser(Long id) {
        return id != null ? userJpaRepository.findById(id).orElse(null) : null;
    }

    private Project getProject(Long id) {
        return id != null ? projectJpaRepository.findById(id).orElse(null) : null;
    }
}
