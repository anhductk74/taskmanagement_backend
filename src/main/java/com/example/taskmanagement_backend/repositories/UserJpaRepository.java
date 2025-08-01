package com.example.taskmanagement_backend.repositories;

import com.example.taskmanagement_backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<User, Long> {
//    Optional<User> findByName(String username);
    List<User> findByUsernameContainingIgnoreCase(String username);
}