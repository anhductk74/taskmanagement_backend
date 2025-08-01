package com.example.taskmanagement_backend.repositories;

import com.example.taskmanagement_backend.dtos.UserDto.UserResponseDto;
import com.example.taskmanagement_backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public interface UserJpaRepository extends JpaRepository<User, Long> {
   Optional<User> findByEmail(String email);

}