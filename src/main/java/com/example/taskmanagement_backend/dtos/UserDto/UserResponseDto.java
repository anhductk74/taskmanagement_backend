package com.example.taskmanagement_backend.dtos.UserDto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {
    private Integer id;
    private String username;
    private String email;
    private String status;
    private String roleName;
    private String organizationName;
}