package com.example.taskmanagement_backend.dtos.UserDto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

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
    private String avt_url;
    private Boolean deleted;

    private String password;

    private List<String> roleNames;

    private Integer organizationId;

    private String status;
}
