package com.example.taskmanagement_backend.controllers;
import com.example.taskmanagement_backend.dtos.UserDto.CreateUserRequestDto;
import com.example.taskmanagement_backend.dtos.UserDto.UpdateUserRequestDto;
import com.example.taskmanagement_backend.dtos.UserDto.UserResponseDto;
import com.example.taskmanagement_backend.entities.User;
import com.example.taskmanagement_backend.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public User createUser(@Valid @RequestBody CreateUserRequestDto dto) {
        return userService.createUser(dto);
    }

//    @PutMapping("/{id}")
//    public User updateUser(@PathVariable Integer id, @Valid @RequestBody UpdateUserRequestDto dto) {
//        return userService.updateUser(id, dto);
//    }

    @GetMapping("/{id}")
    public UserResponseDto getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    @GetMapping
    public List<UserResponseDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
    }
}
