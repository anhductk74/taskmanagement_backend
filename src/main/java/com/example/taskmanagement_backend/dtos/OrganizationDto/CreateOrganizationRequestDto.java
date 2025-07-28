package com.example.taskmanagement_backend.dtos.OrganizationDto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrganizationRequestDto {

    @NotBlank(message = "Organization name is required")
    private String name;

}
