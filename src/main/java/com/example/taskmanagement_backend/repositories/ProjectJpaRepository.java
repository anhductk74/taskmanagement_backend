package com.example.taskmanagement_backend.repositories;

import com.example.taskmanagement_backend.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectJpaRepository extends JpaRepository<Project,Long> {
}
