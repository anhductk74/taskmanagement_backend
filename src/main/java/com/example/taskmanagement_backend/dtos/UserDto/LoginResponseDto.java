package com.example.taskmanagement_backend.dtos.UserDto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoginResponseDto {
    String email;
    Long id;
    String accessToken;
}