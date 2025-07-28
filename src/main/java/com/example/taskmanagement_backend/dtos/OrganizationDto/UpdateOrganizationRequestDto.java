package com.example.taskmanagement_backend.dtos.OrganizationDto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrganizationRequestDto {

    @NotBlank(message = "Organization name is required")
    private String name;
}