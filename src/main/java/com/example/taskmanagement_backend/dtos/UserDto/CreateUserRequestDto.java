package com.example.taskmanagement_backend.dtos.UserDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserRequestDto {

    @NotBlank
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    @NotNull
    private Integer roleId;

    @NotNull
    private Integer organizationId;

    private String status;
}
