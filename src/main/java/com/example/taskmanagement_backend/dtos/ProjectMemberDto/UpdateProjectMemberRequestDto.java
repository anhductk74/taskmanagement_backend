package com.example.taskmanagement_backend.dtos.ProjectMemberDto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProjectMemberRequestDto {

    @NotBlank(message = "Role is required")
    private Long roleId;
}
