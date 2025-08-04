package com.example.taskmanagement_backend.repositories;

import com.example.taskmanagement_backend.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskJpaRepository extends JpaRepository<Task, Long> {
}
