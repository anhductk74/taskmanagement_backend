package com.example.taskmanagement_backend.dtos.TaskDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTaskRequestDto {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotBlank(message = "Status is required")
    private String status;

    @NotBlank(message = "Priority is required")
    private String priority;

    private LocalDate deadline;

    @NotNull(message = "Project ID is required")
    private Long projectId;

    private Long groupId;

    private Long assignedToId;

    @NotNull(message = "Creator ID is required")
    private Long creatorId;
}
