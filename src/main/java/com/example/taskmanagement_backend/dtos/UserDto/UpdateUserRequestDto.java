package com.example.taskmanagement_backend.dtos.UserDto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRequestDto {

    @NotBlank
    private String username;

    @NotBlank
    @Email
    private String email;

    private String password;

    private Integer roleId;

    private Integer organizationId;

    private String status;
}
