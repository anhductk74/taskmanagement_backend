package com.example.taskmanagement_backend.dtos.ProjectDto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProjectRequestDto {

    @NotBlank(message = "Project name is required")
    private String name;

    private String description;

    @NotBlank(message = "Status is required")
    private String status;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotNull(message = "Owner ID is required")
    private Long ownerId;

    @NotNull(message = "Project Manager ID is required")
    private Long pmId;

    @NotNull(message = "Organization ID is required")
    private Long organizationId;
}
