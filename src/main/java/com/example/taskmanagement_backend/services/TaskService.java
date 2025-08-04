package com.example.taskmanagement_backend.services;

import com.example.taskmanagement_backend.dtos.TaskChecklistDto.TaskChecklistResponseDto;
import com.example.taskmanagement_backend.dtos.TaskDto.CreateTaskRequestDto;
import com.example.taskmanagement_backend.dtos.TaskDto.TaskResponseDto;
import com.example.taskmanagement_backend.dtos.TaskDto.UpdateTaskRequestDto;
import com.example.taskmanagement_backend.entities.*;
import com.example.taskmanagement_backend.enums.TaskPriority;
import com.example.taskmanagement_backend.enums.TaskStatus;
import com.example.taskmanagement_backend.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskJpaRepository taskRepository;
    private final ProjectJpaRepository projectJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final TeamJpaRepository teamJpaRepository;

    public TaskResponseDto createTask(CreateTaskRequestDto dto) {
        // Nếu có projectId thì mới tìm Project
        Project project = null;
        if (dto.getProjectId() != null) {
            project = projectJpaRepository.findById(dto.getProjectId())
                    .orElseThrow(() -> new EntityNotFoundException("Project not found"));
        }

        User creator = userJpaRepository.findById(dto.getCreatorId())
                .orElseThrow(() -> new EntityNotFoundException("Creator not found"));

        Team team = null;
        if (dto.getGroupId() != null) {
            team = teamJpaRepository.findById(dto.getGroupId())
                    .orElseThrow(() -> new EntityNotFoundException("Group (Team) not found"));
        }

        Task task = Task.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .status(TaskStatus.valueOf(dto.getStatus()))
                .priority(TaskPriority.valueOf(dto.getPriority()))
                .deadline(dto.getDeadline())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .creator(creator)
                .project(project) // có thể null
                .team(team)       // có thể null
                .build();

        taskRepository.save(task);
        return mapToDto(task);
    }


    public List<TaskResponseDto> getAllTasks() {
        return taskRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public TaskResponseDto getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        return mapToDto(task);
    }

    public TaskResponseDto updateTask(Long id, UpdateTaskRequestDto dto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        if (dto.getTitle() != null) task.setTitle(dto.getTitle());
        if (dto.getDescription() != null) task.setDescription(dto.getDescription());
        if (dto.getStatus() != null) task.setStatus(TaskStatus.valueOf(dto.getStatus()));
        if (dto.getPriority() != null) task.setPriority(TaskPriority.valueOf(dto.getPriority()));
        if (dto.getDeadline() != null) task.setDeadline(dto.getDeadline());
        if (dto.getGroupId() != null) {
            Team team = teamJpaRepository.findById(dto.getGroupId())
                    .orElseThrow(() -> new EntityNotFoundException("Group (Team) not found"));
            task.setTeam(team);
        }

        task.setUpdatedAt(LocalDateTime.now());
        taskRepository.save(task);
        return mapToDto(task);
    }

    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        taskRepository.delete(task);
    }

    private TaskResponseDto mapToDto(Task task) {
        return TaskResponseDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus().name())
                .priority(task.getPriority().name())
                .deadline(task.getDeadline())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .creatorId(task.getCreator() != null ? task.getCreator().getId().longValue() : null)

                .projectId(task.getProject() != null ? task.getProject().getId() : null)
                .groupId(task.getTeam() != null ? task.getTeam().getId() : null)

                .checklists(
                        task.getChecklists() != null
                                ? task.getChecklists().stream()
                                .map(c -> TaskChecklistResponseDto.builder()
                                        .id(c.getId())
                                        .item(c.getItem())
                                        .isCompleted(c.getIsCompleted())
                                        .createdAt(c.getCreatedAt())
                                        .taskId(task.getId())
                                        .build())
                                .collect(Collectors.toList())
                                : null
                )
                .build();




    }

}
