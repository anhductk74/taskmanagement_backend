package com.example.taskmanagement_backend.dtos.TaskDto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseDto {

    private Long id;

    private String title;

    private String description;

    private String status;

    private String priority;

    private LocalDate deadline;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long assignedToId;

    private Long groupId;

    private Long projectId;

    private Long creatorId;
}
