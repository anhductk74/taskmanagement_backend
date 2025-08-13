package com.example.taskmanagement_backend.dtos.TeamMemberDto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTeamMemberRequestDto {

    @NotBlank(message = "Role is required")
    private Long roleId;
}

