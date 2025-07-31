package com.example.taskmanagement_backend.dtos.UserDto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {
    private Integer id;
    private String username;
    private String email;
    private String avt_url;
    private Boolean deleted;
    private String status;
    private List<String> roleNames;
    private String organizationName;
}