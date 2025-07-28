package com.example.taskmanagement_backend.dtos.TaskDto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTaskRequestDto {

    private String title;

    private String description;

    private String status;

    private String priority;

    private LocalDate deadline;

    private Long assignedToId;

    private Long groupId;
}
