package com.example.taskmanagement_backend.controllers;

import com.example.taskmanagement_backend.dtos.TeamDto.CreateTeamResponseDto;
import com.example.taskmanagement_backend.dtos.TeamDto.TeamResponseDto;
import com.example.taskmanagement_backend.dtos.TeamDto.UpdateTeamResponseDto;
import com.example.taskmanagement_backend.services.TeamService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamController {
    @Autowired
    private TeamService teamService;

    @GetMapping
    public List<TeamResponseDto> findAll() {
        return teamService.getAllTeams();
    }

    @GetMapping("/{id}")
    public TeamResponseDto findOne(@PathVariable Long id) {
        return teamService.getTeamById(id);
    }

    @PostMapping
    public TeamResponseDto createTeam(@Valid @RequestBody CreateTeamResponseDto dto) {
        return teamService.createTeams(dto);
    }

    @PutMapping("/{id}")
    public TeamResponseDto updateTeam(@PathVariable Long id, @Valid @RequestBody UpdateTeamResponseDto dto) {
        return teamService.updateTeams(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTeam(@PathVariable Long id) {
        if(teamService.deleteTeamById(id)){
            return ResponseEntity.ok("Delete Team Successfully Id: "+id);
        }
        return ResponseEntity.notFound().build();
    }
    @GetMapping("/project/{projectId}")
    public List<TeamResponseDto> getTeamsByProjectId(@PathVariable Long projectId) {
        return teamService.findByProjectId(projectId);
    }

}
