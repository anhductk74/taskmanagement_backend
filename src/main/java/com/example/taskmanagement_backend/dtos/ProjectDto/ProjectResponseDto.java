package com.example.taskmanagement_backend.dtos.ProjectDto;

import com.example.taskmanagement_backend.entities.Organization;
import com.example.taskmanagement_backend.entities.User;
import com.example.taskmanagement_backend.enums.ProjectStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponseDto {

    private Long id;
    private String name;
    private String description;
    private ProjectStatus status;
    private LocalDate startDate;
    private LocalDate endDate;

    private Long ownerId;
    private Long pmId;
    private Long organizationId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
