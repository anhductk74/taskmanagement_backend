package com.example.taskmanagement_backend.dtos.UserDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

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

    private String avt_url;

    private Boolean deleted;

    @NotNull
    private List<String> roleNames;

    @NotNull
    private Integer organizationId;

    private String status;

}
